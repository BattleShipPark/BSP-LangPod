package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

/**
 */

public class GetMyChannelList implements UseCase<Void, List<ChannelRealm>> {
    private final ChannelDbRepository dbRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    @Inject
    public GetMyChannelList(ChannelDbRepository dbRepository, Scheduler scheduler, Scheduler postScheduler) {
        this.dbRepository = dbRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public Observable<List<ChannelRealm>> execute(Void param) {
        return dbRepository.myChannelList().subscribeOn(scheduler).observeOn(postScheduler);
    }
}
