package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

public class GetMyChannel implements UseCase<MyChannelData, MyChannelRealm> {
    private final ChannelDbRepository dbRepository;
    private final ChannelServerRepository serverRepository;
    private final Scheduler scheduler;
    private Scheduler postScheduler;
    private final DomainMapper domainMapper;

    @Inject
    public GetMyChannel(ChannelDbRepository dbRepository, ChannelServerRepository serverRepository,
                        Scheduler scheduler, Scheduler postScheduler, DomainMapper domainMapper) {
        this.dbRepository = dbRepository;
        this.serverRepository = serverRepository;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        this.domainMapper = domainMapper;
    }

    @Override
    public Observable<MyChannelRealm> execute(MyChannelData localMyChannelData) {
        return Observable.create(subscriber ->
                dbRepository.myChannel(localMyChannelData.id()).subscribe(
                        myChannelRealm -> onDbLoaded(subscriber, localMyChannelData, myChannelRealm),
                        subscriber::onError));
    }

    private void onDbLoaded(Subscriber<? super MyChannelRealm> subscriber, MyChannelData localMyChannelData, MyChannelRealm myChannelRealm) {
        subscriber.onNext(myChannelRealm);

        serverRepository.myChannel(localMyChannelData.url()).subscribeOn(scheduler).observeOn(postScheduler)
                .subscribe(myChannelJson -> onServerLoaded(myChannelRealm, myChannelJson),
                        subscriber::onError, subscriber::onCompleted);
    }

    private void onServerLoaded(MyChannelRealm myChannelRealm, MyChannelJson myChannelJson) {
        dbRepository.putMyChannel(domainMapper.myChannelJsonAsRealm(myChannelRealm, myChannelJson));
    }
}
