package com.battleshippark.bsp_langpod.domain;

import android.content.Context;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.download.DownloadProgressParam;
import com.battleshippark.bsp_langpod.data.download.Downloader;

import java.io.File;

import rx.Observable;
import rx.Scheduler;
import rx.subjects.PublishSubject;

/**
 */
public class DownloadMedia implements UseCase<DownloadMedia.Param, File> {
    private final Downloader downloader;
    private final Context context;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;
    private final PublishSubject<DownloadProgressParam> reposDownloadProgress = PublishSubject.create();

    public DownloadMedia(Context context, Scheduler scheduler, Scheduler postScheduler, AppPhase appPhase, PublishSubject<DownloadProgressParam> downloadProgress) {
        this.context = context;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        this.downloader = new Downloader(appPhase, reposDownloadProgress);
        this.reposDownloadProgress.subscribe(downloadProgress::onNext);
    }

    @Override
    public Observable<File> execute(Param param) {
        String filename = param.url.substring(param.url.lastIndexOf('/') + 1);
        return downloader.download(String.valueOf(param.identifier), param.url, new File(context.getExternalFilesDir(null), filename).getAbsolutePath())
                .subscribeOn(scheduler)
                .observeOn(postScheduler);
    }

    public static class Param {
        public final long identifier;
        public final String url;

        public Param(long identifier, String url) {
            this.identifier = identifier;
            this.url = url;
        }
    }
}
