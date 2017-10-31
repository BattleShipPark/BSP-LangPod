package com.battleshippark.bsp_langpod.service.downloader;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.DownloadDbApi;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;
import com.battleshippark.bsp_langpod.domain.DownloadMedia;
import com.battleshippark.bsp_langpod.domain.GetChannelWithEpisodeId;
import com.battleshippark.bsp_langpod.domain.UpdateEpisode;
import com.battleshippark.bsp_langpod.util.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Action2;
import rx.functions.Actions;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 */

public class DownloaderService extends Service {
    private static final String TAG = DownloaderService.class.getSimpleName();
    static final String ACTION_PROGRESS = TAG + ".actionProgress";
    static final String ACTION_COMPLETED = TAG + ".actionCompleted";
    static final String ACTION_ERROR = TAG + ".actionError";
    private static final Logger logger = new Logger(TAG);
    private static final int NOTIFICATION_ID = -1;
    private final IBinder mBinder = new LocalBinder();
    private final PublishSubject<DownloadProgressParam> progressSubject = PublishSubject.create();
    private final DownloaderQueueManager queueManager = DownloaderQueueManager.getInstance();
    private final CompositeSubscription subscription = new CompositeSubscription();
    private final ParamManager paramManager = new ParamManager();
    private HandlerThread downloadThread, operationThread;
    private Handler operationHandler;
    private DownloadMedia downloadMedia;
    private GetChannelWithEpisodeId getChannelWithEpisodeId;
    private UpdateEpisode updateEpisode;
    private DownloadDbApi downloadDbApi;
    private NotificationController notificationController;

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

        downloadMedia = new DownloadMedia(downloadScheduler, operationScheduler, new AppPhase(BuildConfig.DEBUG));

        ChannelDbApi channelDbApi = DaggerDbApiGraph.create().channelApi();
        getChannelWithEpisodeId = new GetChannelWithEpisodeId(channelDbApi, Schedulers.immediate(), Schedulers.immediate());
        updateEpisode = new UpdateEpisode(channelDbApi, Schedulers.immediate(), Schedulers.immediate());

        downloadDbApi = DaggerDbApiGraph.create().downloadApi();

        notificationController = new NotificationController(this, NOTIFICATION_ID);

        subscription.add(
                progressSubject
                        .throttleLast(1000, TimeUnit.MILLISECONDS, operationScheduler)
                        .subscribe(this::onProgress, logger::w));
        subscription.add(
                downloadDbApi.getNotDownloaded()
                        .subscribeOn(AndroidSchedulers.from(operationHandler.getLooper()))
                        .subscribe(queueManager::clearWith));

        runNext();
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

    private void download(DownloadRealm downloadRealm) {
        startForeground(NOTIFICATION_ID, notificationController.prepare());

        ChannelRealm channelRealm = downloadRealm.getChannelRealm();
        EpisodeRealm episodeRealm = downloadRealm.getEpisodeRealm();

        String fileName = channelRealm.getTitle().replaceAll(" ", "") + "/"
                + episodeRealm.getUrl().substring(episodeRealm.getUrl().lastIndexOf('/') + 1);
        String path = new File(getExternalFilesDir(null), fileName).getAbsolutePath();
        episodeRealm.setDownloadedPath(path);

        downloadMedia.execute(
                new DownloadMedia.Param(String.valueOf(episodeRealm.getId()), episodeRealm.getUrl(), path, progressSubject))
                .subscribe(file -> onCompleted(downloadRealm, file),
                        throwable -> onError(downloadRealm, throwable));

        updateEpisode.execute(episodeRealm).subscribe(Actions.empty(), logger::w);
    }

    private void onCompleted(DownloadRealm downloadRealm, File file) {
        logger.d("onCompleted(): " + file);

        downloadRealm.getEpisodeRealm().setLength(getLength(file));

        sendCompleteBroadcast(downloadRealm.getEpisodeRealm(), file);
        stopForeground(true);
        notificationController.complete();
        queueManager.markComplete(downloadRealm);
        queueManager.remove(downloadRealm);

        getChannel(downloadRealm.getEpisodeId(), (channelRealm, episodeRealm) -> {
            episodeRealm.setDownloadState(EpisodeRealm.DownloadState.DOWNLOADED);
            episodeRealm.setLength(getLength(file));
            logger.d("onCompleted(): " + episodeRealm);
            updateEpisode.execute(episodeRealm).subscribe(Actions.empty(), logger::w);
        });

        runNext();
    }

    private long getLength(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mediaMetadataRetriever.release();
        return Long.parseLong(duration) / 1000;
    }

    private void onError(DownloadRealm downloadRealm, Throwable throwable) {
        sendErrorBroadcast(downloadRealm.getEpisodeRealm(), throwable);
        stopForeground(true);
        notificationController.complete();
        queueManager.markError(downloadRealm);

        getChannel(downloadRealm.getEpisodeId(), (channelRealm, episodeRealm) -> {
            episodeRealm.setDownloadState(EpisodeRealm.DownloadState.FAILED_DOWNLOAD);
            updateEpisode.execute(episodeRealm).subscribe(Actions.empty(), logger::w);
        });
    }

    private void runNext() {
        operationHandler.post(() -> {
            try {
                DownloadRealm downloadRealm = queueManager.peek();
                queueManager.markDownloading(downloadRealm);
                operationHandler.post(() -> download(downloadRealm));
            } catch (InterruptedException e) {
                logger.w(e);
            }
        });
    }

    private void onProgress(DownloadProgressParam param) {
        if (param.done()) {
            return;
        }
        sendProgressBroadcast(param);

        long episodeId = Long.parseLong(param.identifier());
        getChannel(episodeId, (channelRealm, episodeRealm) -> {
            AndroidSchedulers.mainThread().createWorker().schedule(() -> notificationController.update(channelRealm, episodeRealm, param));

            episodeRealm.setDownloadState(EpisodeRealm.DownloadState.DOWNLOADING);
            episodeRealm.setDownloadedBytes(param.bytesRead());
            episodeRealm.setTotalBytes(param.contentLength());
            updateEpisode.execute(episodeRealm).subscribe(Actions.empty(), logger::w);
        });

    }

    private void getChannel(long episodeId, Action2<ChannelRealm, EpisodeRealm> action) {
        getChannelWithEpisodeId.execute(episodeId).subscribe(channelRealm -> {
                    logger.d("getChannel(): %d", episodeId);
                    Stream.of(channelRealm.getEpisodes())
                            .filter(episodeRealm -> episodeRealm.getId() == episodeId)
                            .findFirst()
                            .ifPresent(episodeRealm -> {
                                action.call(channelRealm, episodeRealm);
                            });
                },
                logger::w);
    }

    private void sendProgressBroadcast(DownloadProgressParam param) {
        sendBroadcast(paramManager.getProgressIntent(param));
    }

    private void sendCompleteBroadcast(EpisodeRealm episodeRealm, File file) {
        sendBroadcast(paramManager.getCompleteIntent(episodeRealm, file));
    }

    private void sendErrorBroadcast(EpisodeRealm episodeRealm, Throwable throwable) {
        sendBroadcast(paramManager.getErrorIntent(episodeRealm, throwable));
    }

    class LocalBinder extends Binder {
        DownloaderService getService() {
            return DownloaderService.this;
        }
    }
}
