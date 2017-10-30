package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;
import com.battleshippark.bsp_langpod.data.downloader.Downloader;

import java.io.File;

import rx.Observable;
import rx.Scheduler;
import rx.subjects.PublishSubject;

/**
 */
public class DownloadMedia implements UseCase<DownloadMedia.Param, File> {
    private final Downloader downloader;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    public DownloadMedia(Scheduler scheduler, Scheduler postScheduler, AppPhase appPhase) {
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        this.downloader = new Downloader(appPhase);
    }

    @Override
    public Observable<File> execute(Param param) {
        return downloader.download(param.identifier, param.url, param.path, param.progressSubject)
                .subscribeOn(scheduler)
                .observeOn(postScheduler);
    }

    public static class Param {
        final String identifier;
        final String url;
        final String path;
        final PublishSubject<DownloadProgressParam> progressSubject;

        public Param(String identifier, String url, String path, PublishSubject<DownloadProgressParam> progressSubject) {
            this.identifier = identifier;
            this.url = url;
            this.path = path;
            this.progressSubject = progressSubject;
        }
    }
}
