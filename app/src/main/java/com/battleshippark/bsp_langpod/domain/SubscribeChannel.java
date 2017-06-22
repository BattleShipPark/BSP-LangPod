package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import javax.inject.Inject;

import rx.Observable;

/**
 */

public class SubscribeChannel implements UseCase<ChannelRealm, Void> {
    private final ChannelDbRepository dbRepository;

    @Inject
    public SubscribeChannel(ChannelDbRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    @Override
    public Observable<Void> execute(ChannelRealm channelRealm) {
        return dbRepository.switchSubscribe(channelRealm);
    }
}
