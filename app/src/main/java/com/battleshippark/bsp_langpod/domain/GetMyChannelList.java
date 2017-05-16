package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 */

public class GetMyChannelList implements UseCase<Void, List<MyChannelData>> {
    private final ChannelDbRepository dbRepository;
    private final Executor executor;
    private final RealmMapper mapper;

    @Inject
    public GetMyChannelList(ChannelDbRepository dbRepository, Executor executor, RealmMapper mapper) {
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
