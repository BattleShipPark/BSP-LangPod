package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.DownloadDbRepository;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

/**
 */

public class GetDownloadList implements UseCase<GetDownloadList.Type, List<DownloadRealm>> {
    private final DownloadDbRepository dbRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    @Inject
    public GetDownloadList(DownloadDbRepository dbRepository, Scheduler scheduler, Scheduler postScheduler) {
        this.dbRepository = dbRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public Observable<List<DownloadRealm>> execute(GetDownloadList.Type param) {
        return Observable.create(subscriber -> {
            Observable<List<DownloadRealm>> observable = null;
            switch (param) {
                case ENTIRE:
                    observable = dbRepository.all();
                    break;
                case DOWNLOADED:
                    break;
                case TO_DOWNLOAD:
                    break;
            }
            observable.subscribeOn(scheduler).observeOn(postScheduler)
                    .subscribe(subscriber::onNext, subscriber::onError);
        });
    }

    public enum Type {
        ENTIRE, DOWNLOADED, TO_DOWNLOAD
    }
}
