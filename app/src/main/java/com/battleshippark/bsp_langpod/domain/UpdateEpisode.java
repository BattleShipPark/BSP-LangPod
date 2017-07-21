package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

/**
 */

public class UpdateEpisode implements UseCase<EpisodeRealm, Void> {
    private final ChannelDbRepository dbRepository;
    private final Scheduler scheduler;
    private Scheduler postScheduler;

    @Inject
    public UpdateEpisode(ChannelDbRepository dbRepository, Scheduler scheduler, Scheduler postScheduler) {
        this.dbRepository = dbRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public Observable<Void> execute(EpisodeRealm episodeRealm) {
        return dbRepository.putEpisode(episodeRealm).subscribeOn(scheduler).observeOn(postScheduler);
    }
}
