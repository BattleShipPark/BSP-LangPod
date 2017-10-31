package com.battleshippark.bsp_langpod.service.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerDomainMapperGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.domain.DomainMapper;
import com.battleshippark.bsp_langpod.domain.GetChannel;
import com.battleshippark.bsp_langpod.domain.UpdateEpisode;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

/**
 */

public class PlayerService extends Service {
    private static final String TAG = PlayerService.class.getSimpleName();
    public static final String ACTION_PLAY = TAG + ".actionPlay";
    public static final String ACTION_PAUSE = TAG + ".actionPause";
    public static final String ACTION_PLAYED = TAG + ".actionPlayed";
    public static final String ACTION_PLAYING = TAG + ".actionPlaying";
    private static final Logger logger = new Logger(TAG);
    private static final MediaPlayer mp = new MediaPlayer();
    private final IBinder mBinder = new LocalBinder();
    private final ParamManager paramManager = new ParamManager();
    private HandlerThread thread;
    private Handler handler;
    private GetChannel getChannel;
    private UpdateEpisode updateEpisode;
    private long playingEpisodeId = -1;
    private Observable<Long> timer;
    private Subscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();

        thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());

        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();
        DomainMapper domainMapper = DaggerDomainMapperGraph.create().domainMapper();

        HandlerScheduler handlerScheduler = HandlerScheduler.from(handler);
        getChannel = new GetChannel(channelDbApi, null, handlerScheduler, AndroidSchedulers.mainThread(), domainMapper);
        updateEpisode = new UpdateEpisode(channelDbApi, handlerScheduler, Schedulers.immediate());

        timer = Observable.interval(1, TimeUnit.SECONDS, handlerScheduler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        if (paramManager.hasServiceIntent(intent)) {
            long channelId = paramManager.getChannelId(intent);
            long episodeId = paramManager.getEpisodeId(intent);
            if (channelId > 0 && episodeId > 0) {
                if (paramManager.hasPlayAction(intent)) {
                    play(channelId, episodeId);
                } else if (paramManager.hasPauseAction(intent)) {
                    pause(channelId, episodeId);
                }
            }
        }
        return ret;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.interrupt();
    }

    private void play(long channelId, long episodeId) {
        getChannel.execute(new GetChannel.Param(channelId, GetChannel.Type.ONLY_DB)).subscribe(channelRealm -> {
            for (EpisodeRealm episodeRealm : channelRealm.getEpisodes()) {
                if (episodeRealm.getId() == episodeId) {
                    play(channelRealm, episodeRealm);
                    break;
                }
            }
        });
    }

    public void play(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        handler.post(() -> {
            try {
                if (playingEpisodeId != episodeRealm.getId()) {
                    mp.stop();
                    mp.reset();
                    mp.setDataSource("file://" + episodeRealm.getDownloadedPath());
                    mp.setOnCompletionListener(mp1 -> {
                        unsubscribeTimer();
                        updateEpisode(episodeRealm, EpisodeRealm.PlayState.PLAYED);
                        sendPlayedBroadcast(episodeRealm.getId());
                        AndroidSchedulers.mainThread().createWorker().schedule(() -> showNotification(channelRealm, episodeRealm, true));
                    });
                    mp.prepare();
                    mp.seekTo(episodeRealm.getPlayTimeInMs());
                }
                mp.start();

                playingEpisodeId = episodeRealm.getId();

                unsubscribeTimer();
                subscription = timer.subscribe(aLong -> {
                    updatePlayTime(mp.getCurrentPosition(), episodeRealm);
                    sendPlayingBroadcast(episodeRealm.getId(), mp.getCurrentPosition());
                });

                updateEpisode(episodeRealm, EpisodeRealm.PlayState.PLAYING);
                sendPlayBroadcast(episodeRealm.getId());
                AndroidSchedulers.mainThread().createWorker().schedule(() -> showNotification(channelRealm, episodeRealm, true));
            } catch (IOException e) {
                logger.w(e);
                playingEpisodeId = -1;
                cancelNotification(channelRealm);
                unsubscribeTimer();
                updateEpisode(episodeRealm, EpisodeRealm.PlayState.PAUSE);
            }
        });
    }

    private void unsubscribeTimer() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void updateEpisode(EpisodeRealm episodeRealm, EpisodeRealm.PlayState state) {
        episodeRealm.setPlayState(state);
        updateEpisode.execute(episodeRealm).subscribe(Actions.empty(), logger::w);
    }

    private void updatePlayTime(int currentPosition, EpisodeRealm episodeRealm) {
        episodeRealm.setPlayTimeInMs(currentPosition);
        updateEpisode.execute(episodeRealm).subscribe(Actions.empty(), logger::w);
        logger.d("updatePlayTime: %d, %s", currentPosition, episodeRealm);
    }

    private void pause(long channelId, long episodeId) {
        getChannel.execute(new GetChannel.Param(channelId, GetChannel.Type.ONLY_DB)).subscribe(channelRealm -> {
            for (EpisodeRealm episodeRealm : channelRealm.getEpisodes()) {
                if (episodeRealm.getId() == episodeId) {
                    pause(channelRealm, episodeRealm);
                    break;
                }
            }
        });
    }

    public void pause(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        handler.post(() -> {
            if (mp.isPlaying()) {
                mp.pause();
                unsubscribeTimer();
                updateEpisode(episodeRealm, EpisodeRealm.PlayState.PAUSE);
                AndroidSchedulers.mainThread().createWorker().schedule(() -> showNotification(channelRealm, episodeRealm, false));
            }
        });
        sendPauseBroadcast(episodeRealm.getId());
    }

    private void showNotification(ChannelRealm channelRealm, EpisodeRealm episodeRealm, boolean isPlaying) {
        PendingIntent pendingIntent = createPendingIntent(isPlaying, channelRealm.getId(), episodeRealm.getId());

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification_play);
        rv.setImageViewResource(R.id.image_iv, R.mipmap.ic_launcher);
        rv.setTextViewText(R.id.channel_tv, channelRealm.getTitle());
        rv.setTextViewText(R.id.episode_tv, episodeRealm.getTitle());
        rv.setImageViewResource(R.id.play_iv, isPlaying ? R.drawable.pause : R.drawable.play);
        rv.setOnClickPendingIntent(R.id.play_iv, pendingIntent);

        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.download)
                .setContent(rv);
        final Notification notification = mBuilder.build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify((int) channelRealm.getId(), notification);

        NotificationTarget notificationTarget = new NotificationTarget(
                this,
                rv,
                R.id.image_iv,
                notification,
                (int) channelRealm.getId());
        Glide.with(getApplicationContext()).load(channelRealm.getImage()).asBitmap().into(notificationTarget);
    }

    private void cancelNotification(ChannelRealm channelRealm) {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int) channelRealm.getId());
    }

    private PendingIntent createPendingIntent(boolean isPlaying, long channelId, long episodeId) {
        Intent intent = paramManager.getServiceIntent(this, isPlaying, channelId, episodeId);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendPlayBroadcast(long episodeId) {
        sendBroadcast(paramManager.getPlayIntent(episodeId));
    }

    private void sendPauseBroadcast(long episodeId) {
        sendBroadcast(paramManager.getPauseIntent(episodeId));
    }

    private void sendPlayedBroadcast(long episodeId) {
        sendBroadcast(paramManager.getPlayedIntent(episodeId));
    }

    private void sendPlayingBroadcast(long episodeId, int currentPosition) {
        sendBroadcast(paramManager.getPlayingIntent(episodeId, currentPosition));
    }

    class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }
}
