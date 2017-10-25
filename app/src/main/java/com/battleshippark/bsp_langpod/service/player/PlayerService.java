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
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;

/**
 */

public class PlayerService extends Service {
    private static final String TAG = PlayerService.class.getSimpleName();
    public static final String ACTION_PLAY = TAG + ".actionPlay";
    public static final String ACTION_PAUSE = TAG + ".actionPause";
    public static final String KEY_CHANNEL_ID = "keyChannelId";
    public static final String KEY_EPISODE_ID = "keyEpisodeId";
    private static final Logger logger = new Logger(TAG);
    private static final MediaPlayer mp = new MediaPlayer();
    private static final Intent playIntent = new Intent(ACTION_PLAY);
    private static final Intent pauseIntent = new Intent(ACTION_PAUSE);
    private final IBinder mBinder = new LocalBinder();
    private HandlerThread thread;
    private Handler handler;
    private GetChannel getChannel;
    private long playingEpisodeId = -1;


    @Override
    public void onCreate() {
        super.onCreate();

        thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());

        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();
        DomainMapper domainMapper = DaggerDomainMapperGraph.create().domainMapper();

        HandlerScheduler handlerScheduler = HandlerScheduler.from(handler);
        getChannel = new GetChannel(channelDbApi, null, handlerScheduler, handlerScheduler, domainMapper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                long channelId = intent.getLongExtra(KEY_CHANNEL_ID, -1);
                long episodeId = intent.getLongExtra(KEY_EPISODE_ID, -1);
                if (channelId > 0 && episodeId > 0) {
                    if (action.equals(ACTION_PLAY)) {
                        play(channelId, episodeId);
                    } else if (action.equals(ACTION_PAUSE)) {
                        pause(channelId, episodeId);
                    }
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
                    mp.prepare();
                }
                mp.start();
                playingEpisodeId = episodeRealm.getId();
                sendBroadcast(playIntent, episodeRealm.getId());
            } catch (IOException e) {
                logger.w(e);
                playingEpisodeId = -1;
                cancelNotification(channelRealm);
            }
        });
        showNotification(channelRealm, episodeRealm, true);
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
                AndroidSchedulers.mainThread().createWorker().schedule(() -> showNotification(channelRealm, episodeRealm, false));
            }
        });
        sendBroadcast(pauseIntent, episodeRealm.getId());
    }

    private void showNotification(ChannelRealm channelRealm, EpisodeRealm episodeRealm, boolean isPlaying) {
        PendingIntent pendingIntent = createPendingIntent(isPlaying);

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

    private PendingIntent createPendingIntent(boolean isPlaying) {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(isPlaying ? PlayerService.ACTION_PAUSE : PlayerService.ACTION_PLAY);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendBroadcast(Intent intent, long episodeId) {
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }
}
