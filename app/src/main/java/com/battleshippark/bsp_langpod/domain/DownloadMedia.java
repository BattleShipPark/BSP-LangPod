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

    public DownloadMedia(Context context, Scheduler scheduler, Scheduler postScheduler, AppPhase appPhase) {
        this.context = context;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        this.downloader = new Downloader(appPhase);
    }

    @Override
    public Observable<File> execute(Param param) {
        String filename = param.url.substring(param.url.lastIndexOf('/') + 1);
        return downloader.download(param.identifier, param.url, new File(context.getExternalFilesDir(null), filename).getAbsolutePath(), param.progressSubject)
                .subscribeOn(scheduler)
                .observeOn(postScheduler);
    }

    public static class Param {
        public final String identifier;
        public final String url;
        public final PublishSubject<DownloadProgressParam> progressSubject;

        public Param(String identifier, String url, PublishSubject<DownloadProgressParam> progressSubject) {
            this.identifier = identifier;
            this.url = url;
            this.progressSubject = progressSubject;
        }
    }
}
