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
    private Scheduler scheduler;

    @Inject
    public SubscribeChannel(ChannelDbRepository dbRepository, Scheduler scheduler) {
        this.dbRepository = dbRepository;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Void> execute(ChannelRealm channelRealm) {
        return dbRepository.switchSubscribe(channelRealm)
                .subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .toObservable();
    }
}
