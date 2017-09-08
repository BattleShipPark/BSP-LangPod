package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

/**
 */

public class SubscribeChannel implements UseCase<ChannelRealm, Boolean> {
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
    public Observable<Boolean> execute(ChannelRealm channelRealm) {
        return dbRepository.switchSubscribe(channelRealm)
                .subscribeOn(scheduler).observeOn(postScheduler);
    }
}
