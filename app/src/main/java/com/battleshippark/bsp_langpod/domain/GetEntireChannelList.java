package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

public class GetEntireChannelList implements UseCase<GetEntireChannelList.Type, List<ChannelRealm>> {
    private final ChannelDbRepository dbRepository;
    private final ChannelServerRepository serverRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;
    private final DomainMapper domainMapper;

    @Inject
    public GetEntireChannelList(ChannelDbRepository dbRepository, ChannelServerRepository serverRepository,
                                Scheduler scheduler, Scheduler postScheduler, DomainMapper domainMapper) {
        this.dbRepository = dbRepository;
        this.serverRepository = serverRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        this.domainMapper = domainMapper;
    }

    @Override
    public Observable<List<ChannelRealm>> execute(GetEntireChannelList.Type param) {
        return Observable.create(subscriber ->
                dbRepository.entireChannelList().subscribeOn(scheduler).observeOn(postScheduler)
                        .subscribe(
                                entireChannelRealmList -> onDbLoaded(subscriber, param, entireChannelRealmList),
                                subscriber::onError));
    }

    private void onDbLoaded(Subscriber<? super List<ChannelRealm>> subscriber, Type param, List<ChannelRealm> channelRealmList) {
        try {
            subscriber.onNext(channelRealmList);

            if (param == Type.DB_AND_SERVER) {
                serverRepository.entireChannelList().subscribeOn(scheduler).observeOn(postScheduler)
                        .subscribe(
                                channelListJson -> onServerLoaded(subscriber, channelRealmList, channelListJson),
                                subscriber::onError, subscriber::onCompleted);
            } else {
                subscriber.onCompleted();
            }
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private void onServerLoaded(Subscriber<? super List<ChannelRealm>> subscriber, List<ChannelRealm> localChannelRealmList, EntireChannelListJson entireChannelListJson) {
        List<ChannelRealm> channelRealmList = domainMapper.entireChannelListJsonAsRealm(localChannelRealmList, entireChannelListJson);
        subscriber.onNext(channelRealmList);
        dbRepository.putEntireChannelList(channelRealmList)
                .subscribeOn(scheduler).observeOn(postScheduler)
                .subscribe(subscriber::onCompleted, subscriber::onError);
    }

    public enum Type {
        ONLY_DB,
        DB_AND_SERVER
    }
}
