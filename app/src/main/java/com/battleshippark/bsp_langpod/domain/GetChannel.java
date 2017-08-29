package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

public class GetChannel implements UseCase<Long, List<ChannelRealm>> {
    private final ChannelDbRepository dbRepository;
    private final ChannelServerRepository serverRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;
    private final DomainMapper domainMapper;

    public GetChannel(ChannelDbRepository dbRepository, ChannelServerRepository serverRepository,
                      Scheduler scheduler, Scheduler postScheduler, DomainMapper domainMapper) {
        this.dbRepository = dbRepository;
        this.serverRepository = serverRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        this.domainMapper = domainMapper;
    }

    @Override
    public Observable<List<ChannelRealm>> execute(Long id) {
        return Observable.create(subscriber ->
                dbRepository.channel(id).subscribeOn(scheduler)
                        .subscribe(channelRealmList -> onDbLoaded(subscriber, channelRealmList),
                                subscriber::onError));
    }

    private void onDbLoaded(Subscriber<? super List<ChannelRealm>> subscriber, List<ChannelRealm> channelRealmList) {
        subscriber.onNext(channelRealmList);

        if (serverRepository == null) {
            subscriber.onCompleted();
        } else {
            serverRepository.myChannel(channelRealmList.get(0).getUrl()).subscribeOn(scheduler)
                    .subscribe(myChannelJson -> onServerLoaded(subscriber, channelRealmList.get(0), myChannelJson),
                            subscriber::onError, subscriber::onCompleted);
        }
    }

    private void onServerLoaded(Subscriber<? super List<ChannelRealm>> subscriber, ChannelRealm channelRealm, ChannelJson channelJson) {
        dbRepository.putChannel(domainMapper.channelJsonAsRealm(channelRealm, channelJson))
                .subscribe(subscriber::onCompleted, subscriber::onError);
    }
}
