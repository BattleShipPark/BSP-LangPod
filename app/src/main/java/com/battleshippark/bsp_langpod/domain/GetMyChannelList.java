package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;

/**
 */

public class GetMyChannelList implements UseCase<Void, List<MyChannelRealm>> {
    private final ChannelDbRepository dbRepository;
    private final DomainMapper domainMapper;

    @Inject
    public GetMyChannelList(ChannelDbRepository dbRepository, DomainMapper domainMapper) {
        this.dbRepository = dbRepository;
        this.domainMapper = domainMapper;
    }

    @Override
    public Observable<List<MyChannelRealm>> execute(Void param) {
        return dbRepository.myChannelList();
    }
}
