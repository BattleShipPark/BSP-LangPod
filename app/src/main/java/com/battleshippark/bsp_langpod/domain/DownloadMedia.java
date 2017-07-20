package com.battleshippark.bsp_langpod.domain;

import android.content.Context;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.download.DownloadListener;
import com.battleshippark.bsp_langpod.data.download.Downloader;

import java.io.File;

import rx.Observable;
import rx.Scheduler;

/**
 */
public class DownloadMedia {
    private final Downloader downloader;
    private final Context context;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    public DownloadMedia(Context context, Scheduler scheduler, Scheduler postScheduler, AppPhase appPhase, DownloadListener downloadListener) {
        this.context = context;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        Scheduler.Worker worker = postScheduler.createWorker();
        this.downloader = new Downloader(appPhase,
                (identifier, bytesRead, contentLength, done) -> {
                    worker.schedule(() ->
                            downloadListener.update(identifier, bytesRead, contentLength, done));
                });
    }

    public Observable<File> download(long identifier, String url) {
        String filename = url.substring(url.lastIndexOf('/') + 1);
        return downloader.download(String.valueOf(identifier), url, new File(context.getExternalFilesDir(null), filename).getAbsolutePath())
                .subscribeOn(scheduler)
                .observeOn(postScheduler);
    }
}
