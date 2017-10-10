package com.battleshippark.bsp_langpod.service.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadCompleteParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadErrorParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;
import com.battleshippark.bsp_langpod.domain.DownloadMedia;
import com.battleshippark.bsp_langpod.domain.GetChannelWithEpisodeId;
import com.battleshippark.bsp_langpod.service.DownloaderNotificationController;
import com.battleshippark.bsp_langpod.util.Logger;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
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
    private static final int NOTIFICATION_ID = -1;
    private final IBinder mBinder = new LocalBinder();
    private final PublishSubject<DownloadProgressParam> progressSubject = PublishSubject.create();
    private final Queue<EpisodeRealm> queue = new ConcurrentLinkedQueue<>();
    private HandlerThread downloadThread, operationThread;
    private Handler operationHandler;
    private DownloadMedia downloadMedia;
    private GetChannelWithEpisodeId getChannelWithEpisodeId;
    private CompositeSubscription subscription = new CompositeSubscription();
    private DownloaderNotificationController notificationController;

    @Override
    public void onCreate() {
        super.onCreate();

        downloadThread = new HandlerThread(TAG + ".download");
        downloadThread.start();
        Handler downloadHandler = new Handler(downloadThread.getLooper());

        operationThread = new HandlerThread(TAG + ".operation");
        operationThread.start();
        operationHandler = new Handler(operationThread.getLooper());

        HandlerScheduler downloadScheduler = HandlerScheduler.from(downloadHandler);
        HandlerScheduler operationScheduler = HandlerScheduler.from(operationHandler);
        downloadMedia = new DownloadMedia(this, downloadScheduler, operationScheduler, new AppPhase(BuildConfig.DEBUG));
        getChannelWithEpisodeId = new GetChannelWithEpisodeId(DaggerDbApiGraph.create().channelApi(), operationScheduler, operationScheduler);

        notificationController = new DownloaderNotificationController(this, NOTIFICATION_ID);

        subscription.add(
                progressSubject
                        .throttleLast(1000, TimeUnit.MILLISECONDS, operationScheduler)
                        .subscribe(this::onProgress, logger::w));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        downloadThread.interrupt();
        operationThread.interrupt();
        subscription.unsubscribe();
    }

    public void enqueue(EpisodeRealm episodeRealm) {
        if (queue.isEmpty()) {
            operationHandler.post(() -> download(episodeRealm));
        }
        queue.offer(episodeRealm);
    }

    public void download(EpisodeRealm episodeRealm) {
        startForeground(NOTIFICATION_ID, notificationController.create());

        downloadMedia.execute(new DownloadMedia.Param(String.valueOf(episodeRealm.getId()), episodeRealm.getUrl(), progressSubject))
                .subscribe(file -> onCompleted(episodeRealm, file),
                        throwable -> sendErrorBroadcast(episodeRealm, throwable));
    }

    private void onCompleted(EpisodeRealm episodeRealm, File file) {
        sendCompleteBroadcast(episodeRealm, file);
        stopForeground(true);
        queue.poll();

        if (!queue.isEmpty()) {
            EpisodeRealm realm = queue.peek();
            operationHandler.post(() -> download(realm));
        }
    }

    private void onProgress(DownloadProgressParam param) {
        long episodeId = Long.parseLong(param.identifier());

        getChannelWithEpisodeId.execute(episodeId).subscribe(channelRealm -> {
                    Stream.of(channelRealm.getEpisodes())
                            .filter(episodeRealm -> episodeRealm.getId() == episodeId)
                            .findFirst()
                            .ifPresent(episodeRealm -> {
                                AndroidSchedulers.mainThread().createWorker().schedule(() -> notificationController.update(channelRealm, episodeRealm, param));
                            });
                },
                logger::w);
        sendProgressBroadcast(param);
    }

    private void sendProgressBroadcast(DownloadProgressParam param) {
        Intent intent = new Intent(ACTION_PROGRESS);
        intent.putExtra(KEY_PROGRESS, param);
        sendBroadcast(intent);
    }

    private void sendCompleteBroadcast(EpisodeRealm episodeRealm, File file) {
        Intent intent = new Intent(ACTION_COMPLETED);
        intent.putExtra(KEY_COMPLETE, DownloadCompleteParam.create(String.valueOf(episodeRealm.getId()), file));
        sendBroadcast(intent);
    }

    private void sendErrorBroadcast(EpisodeRealm episodeRealm, Throwable throwable) {
        Intent intent = new Intent(ACTION_ERROR);
        intent.putExtra(KEY_ERROR, DownloadErrorParam.create(String.valueOf(episodeRealm.getId()), throwable));
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
