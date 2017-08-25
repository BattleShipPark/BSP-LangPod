package com.battleshippark.bsp_langpod.service.downloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import com.battleshippark.bsp_langpod.data.downloader.DownloadCompleteParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadErrorParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;
import com.battleshippark.bsp_langpod.domain.DownloadMedia;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 */

public class DownloaderService extends Service {
    private static final String TAG = DownloaderService.class.getSimpleName();
    public static final String ACTION_PROGRESS = TAG + ".actionProgress";
    public static final String ACTION_COMPLETED = TAG + ".actionCompleted";
    public static final String ACTION_ERROR = TAG + ".actionError";
    public static final String KEY_CHANNEL_ID = "keyChannelId";
    public static final String KEY_EPISODE_ID = "keyEpisodeId";
    public static final String KEY_PROGRESS = "keyProgress";
    public static final String KEY_COMPLETE = "keyComplete";
    private static final String KEY_ERROR = "keyError";
    private static final Logger logger = new Logger(TAG);
    private final IBinder mBinder = new LocalBinder();
    private HandlerThread thread;
    private Handler handler;
    private DownloadMedia downloadMedia;
    private CompositeSubscription subscription = new CompositeSubscription();
    private final PublishSubject<DownloadProgressParam> progressSubject = PublishSubject.create();

    @Override
    public void onCreate() {
        super.onCreate();

        thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());

        downloadMedia = new DownloadMedia(this, HandlerScheduler.from(handler), AndroidSchedulers.mainThread(), new AppPhase(BuildConfig.DEBUG));

        subscription.add(
                progressSubject
                        .throttleLast(1000, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onProgress, logger::w));
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
        subscription.unsubscribe();
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

    public void download(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        downloadMedia.execute(new DownloadMedia.Param(String.valueOf(episodeRealm.getId()), episodeRealm.getUrl(), progressSubject))
                .subscribe(file -> sendCompleteBroadcast(episodeRealm, file),
                        throwable -> sendErrorBroadcast(episodeRealm, throwable));

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
//        intent.setAction(isPlaying ? DownloaderService.ACTION_PAUSE : DownloaderService.ACTION_PLAY);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void onProgress(DownloadProgressParam param) {
        long episodeId = Long.parseLong(param.identifier);


//        showNotification(channelRealm, episodeRealm);
        sendProgressBroadcast(param);
    }

    private void sendProgressBroadcast(DownloadProgressParam param) {
        Intent intent = new Intent(ACTION_PROGRESS);
        intent.putExtra(KEY_PROGRESS, param);
        sendBroadcast(intent);
    }

    private void sendCompleteBroadcast(EpisodeRealm episodeRealm, File file) {
        Intent intent = new Intent(ACTION_COMPLETED);
        intent.putExtra(KEY_COMPLETE, new DownloadCompleteParam(episodeRealm.getId(), file));
        sendBroadcast(intent);
    }

    private void sendErrorBroadcast(EpisodeRealm episodeRealm, Throwable throwable) {
        Intent intent = new Intent(ACTION_ERROR);
        intent.putExtra(KEY_ERROR, new DownloadErrorParam(episodeRealm.getId(), throwable));
        sendBroadcast(intent);
    }

    public DownloadProgressParam getProgressParam(Intent intent) {
        return intent.getParcelableExtra(DownloaderService.KEY_PROGRESS);
    }

    public DownloadCompleteParam getCompleteParam(Intent intent) {
        return intent.getParcelableExtra(DownloaderService.KEY_COMPLETE);
    }

    public DownloadErrorParam getErrorParam(Intent intent) {
        return intent.getParcelableExtra(DownloaderService.KEY_ERROR);
    }

    public class LocalBinder extends Binder {
        DownloaderService getService() {
            return DownloaderService.this;
        }
    }
}
