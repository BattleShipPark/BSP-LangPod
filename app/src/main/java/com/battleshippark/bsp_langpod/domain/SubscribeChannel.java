package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 */

public class SubscribeChannel implements UseCase<ChannelRealm, Void> {
    private final ChannelDbRepository dbRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    @Inject
    public SubscribeChannel(ChannelDbRepository dbRepository, Scheduler scheduler, Scheduler postScheduler) {
        this.dbRepository = dbRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public Observable<Void> execute(ChannelRealm channelRealm) {
        return dbRepository.switchSubscribe(channelRealm)
                .subscribeOn(scheduler).observeOn(postScheduler)
                .toObservable();
    }
}
