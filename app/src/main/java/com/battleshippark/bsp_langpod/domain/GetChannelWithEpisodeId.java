package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import rx.Observable;
import rx.Scheduler;

/**
 */

public class GetChannelWithEpisodeId implements UseCase<Long, ChannelRealm> {
    private final ChannelDbRepository dbRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    public GetChannelWithEpisodeId(ChannelDbRepository dbRepository, Scheduler scheduler, Scheduler postScheduler) {
        this.dbRepository = dbRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public Observable<ChannelRealm> execute(Long episodeId) {
        return dbRepository.channelWithEpisodeId(episodeId)
                .subscribeOn(scheduler).observeOn(postScheduler);
    }
}
