package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 */

public class GetEntireChannelList implements UseCase<Void, List<EntireChannelData>> {
    private final ChannelDbRepository dbRepository;
    private final ChannelServerRepository apiRepository;
    private final Executor executor;
    private final RealmMapper mapper;

    @Inject
    public GetEntireChannelList(ChannelDbRepository dbRepository, ChannelServerRepository apiRepository, Executor executor, RealmMapper mapper) {
        this.dbRepository = dbRepository;
        this.apiRepository = apiRepository;
        this.executor = executor;
        this.mapper = mapper;
    }

    @Override
    public Observable<List<EntireChannelData>> execute(Void param) {
        return Observable.create(subscriber ->
                dbRepository.entireChannelList().subscribe(
                        entireChannelRealmList -> onDbLoaded(subscriber, entireChannelRealmList),
                        subscriber::onError, subscriber::onCompleted));
    }

    private void onDbLoaded(Subscriber<? super List<EntireChannelData>> subscriber, List<EntireChannelRealm> entireChannelRealmList) {
        subscriber.onNext(mapper.asData(entireChannelRealmList));

        apiRepository.entireChannelList().subscribe(
                entireChannelListJson -> onServerLoaded(subscriber, entireChannelListJson),
                subscriber::onError, subscriber::onCompleted);
    }

    private void onServerLoaded(Subscriber<? super List<EntireChannelData>> subscriber, EntireChannelListJson entireChannelListJson) {
        subscriber.onNext(mapper.asData(entireChannelListJson));

        try {
            dbRepository.putEntireChannelList(mapper.entireChannelListJsonAsRealm(entireChannelListJson));
        } catch (Exception e) {
            subscriber.onError(e);
        }

        subscriber.onCompleted();
    }
}
