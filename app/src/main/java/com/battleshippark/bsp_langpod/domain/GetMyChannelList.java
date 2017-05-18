package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;

/**
 */

public class GetMyChannelList implements UseCase<Void, List<MyChannelData>> {
    private final ChannelDbRepository dbRepository;
    private final Executor executor;
    private final Mapper mapper;

    @Inject
    public GetMyChannelList(ChannelDbRepository dbRepository, Executor executor, Mapper mapper) {
        this.dbRepository = dbRepository;
        this.executor = executor;
        this.mapper = mapper;
    }

    @Override
    public Observable<List<MyChannelData>> execute(Void param) {
        return Observable.create(subscriber ->
                dbRepository.myChannelList().subscribe(
                        myChannelRealmList -> subscriber.onNext(mapper.myChannelRealmAsData(myChannelRealmList)),
                        subscriber::onError, subscriber::onCompleted));
    }
}
