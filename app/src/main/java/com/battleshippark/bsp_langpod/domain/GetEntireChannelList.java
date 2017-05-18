package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

public class GetEntireChannelList implements UseCase<Void, List<EntireChannelData>> {
    private final ChannelDbRepository dbRepository;
    private final ChannelServerRepository apiRepository;
    private final Scheduler scheduler;
    private final Mapper mapper;

    @Inject
    public GetEntireChannelList(ChannelDbRepository dbRepository, ChannelServerRepository apiRepository, Scheduler scheduler, Mapper mapper) {
        this.dbRepository = dbRepository;
        this.apiRepository = apiRepository;
        this.scheduler = scheduler;
        this.mapper = mapper;
    }

    @Override
    public Observable<List<EntireChannelData>> execute(Void param) {
        return Observable.create(subscriber ->
                dbRepository.entireChannelList().subscribe(
                        entireChannelRealmList -> onDbLoaded(subscriber, entireChannelRealmList),
                        subscriber::onError));
    }

    private void onDbLoaded(Subscriber<? super List<EntireChannelData>> subscriber, List<EntireChannelRealm> entireChannelRealmList) {
        try {
            subscriber.onNext(mapper.asData(entireChannelRealmList));

            apiRepository.entireChannelList().subscribeOn(scheduler).subscribe(
                    this::onServerLoaded,
                    subscriber::onError, subscriber::onCompleted);
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private void onServerLoaded(EntireChannelListJson entireChannelListJson) {
        dbRepository.putEntireChannelList(mapper.entireChannelListJsonAsRealm(entireChannelListJson));
    }
}
