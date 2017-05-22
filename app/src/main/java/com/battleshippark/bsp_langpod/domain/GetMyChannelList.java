package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 */

public class GetMyChannelList implements UseCase<Void, List<ChannelRealm>> {
    private final ChannelDbRepository dbRepository;

    @Inject
    public GetMyChannelList(ChannelDbRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    @Override
    public Observable<List<ChannelRealm>> execute(Void param) {
        return dbRepository.myChannelList();
    }
}
