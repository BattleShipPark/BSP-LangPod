package com.battleshippark.bsp_langpod.service.downloader;

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

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;
import com.battleshippark.bsp_langpod.domain.DownloadMedia;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.io.File;

import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.subjects.PublishSubject;

/**
 */

public class DownloaderService extends Service {
    public static final String ACTION_PLAY = "actionPlay";
    public static final String ACTION_PAUSE = "actionPause";
    public static final String KEY_CHANNEL_ID = "keyChannelId";
    public static final String KEY_EPISODE_ID = "keyEpisodeId";
    private static final String TAG = DownloaderService.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);
    private static final MediaPlayer mp = new MediaPlayer();
    private static final Intent playIntent = new Intent(ACTION_PLAY);
    private static final Intent pauseIntent = new Intent(ACTION_PAUSE);
    private final IBinder mBinder = new LocalBinder();
    private HandlerThread thread;
    private Handler handler;
    private DownloadMedia downloadMedia;
    private long playingEpisodeId = -1;
    private final PublishSubject<DownloadProgressParam> internalProgressSubject = PublishSubject.create();
    private PublishSubject<DownloadProgressParam> progressSubject;


    @Override
    public void onCreate() {
        super.onCreate();

        thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());

        downloadMedia = new DownloadMedia(this, HandlerScheduler.from(handler), AndroidSchedulers.mainThread(), new AppPhase(BuildConfig.DEBUG));

        internalProgressSubject.subscribe(downloadProgressParam -> {
            if (progressSubject != null && !progressSubject.hasCompleted()) {
                progressSubject.onNext(downloadProgressParam);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
/*        if (intent != null) {
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
        }*/
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

/*    private void play(long channelId, long episodeId) {
        getChannel.execute(channelId).subscribe(channelRealms -> {
            for (EpisodeRealm episodeRealm : channelRealms.get(0).getEpisodes()) {
                if (episodeRealm.getId() == episodeId) {
                    play(channelRealms.get(0), episodeRealm);
                    break;
                }
            }
        });
    }*/

    public void download(ChannelRealm channelRealm, EpisodeRealm episodeRealm, PublishSubject<File> resultSubject) {
        downloadMedia.execute(new DownloadMedia.Param(String.valueOf(episodeRealm.getId()), episodeRealm.getUrl(), internalProgressSubject))
                .subscribe(resultSubject);
        showNotification(channelRealm, episodeRealm);
    }

/*    private void pause(long channelId, long episodeId) {
        getChannel.execute(channelId).subscribe(channelRealms -> {
            for (EpisodeRealm episodeRealm : channelRealms.get(0).getEpisodes()) {
                if (episodeRealm.getId() == episodeId) {
                    pause(channelRealms.get(0), episodeRealm);
                    break;
                }
            }
        });
    }*/

/*    public void pause(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        handler.post(() -> {
            if (mp.isPlaying()) {
                mp.pause();
                AndroidSchedulers.mainThread().createWorker().schedule(() -> showNotification(channelRealm, episodeRealm, false));
            }
        });
        sendBroadcast(pauseIntent, episodeRealm.getId());
    }*/

    private void showNotification(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
//        PendingIntent pendingIntent = createPendingIntent();

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification_download);
        rv.setImageViewResource(R.id.image_iv, R.mipmap.ic_launcher);
        rv.setTextViewText(R.id.channel_tv, channelRealm.getTitle());
        rv.setTextViewText(R.id.episode_tv, episodeRealm.getTitle());

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
        Intent intent = new Intent(this, DownloaderService.class);
        intent.setAction(isPlaying ? DownloaderService.ACTION_PAUSE : DownloaderService.ACTION_PLAY);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendBroadcast(Intent intent, long episodeId) {
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        sendBroadcast(intent);
    }

    public void setProgressSubject(PublishSubject<DownloadProgressParam> progressSubject) {
        this.progressSubject = progressSubject;
    }

    public class LocalBinder extends Binder {
        DownloaderService getService() {
            return DownloaderService.this;
        }
    }
}
