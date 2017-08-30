package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

public class GetChannel implements UseCase<Long, ChannelRealm> {
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
    public Observable<ChannelRealm> execute(Long id) {
        return Observable.create(subscriber ->
                dbRepository.channel(id).subscribeOn(scheduler).observeOn(postScheduler)
                        .subscribe(channelRealm -> onDbLoaded(subscriber, channelRealm),
                                subscriber::onError));
    }

    private void onDbLoaded(Subscriber<? super ChannelRealm> subscriber, ChannelRealm channelRealm) {
        subscriber.onNext(channelRealm);

        if (serverRepository == null) {
            subscriber.onCompleted();
        } else {
            serverRepository.myChannel(channelRealm.getUrl()).subscribeOn(scheduler).observeOn(postScheduler)
                    .subscribe(myChannelJson -> onServerLoaded(subscriber, channelRealm, myChannelJson),
                            subscriber::onError, subscriber::onCompleted);
        }
    }

    private void onServerLoaded(Subscriber<? super ChannelRealm> subscriber, ChannelRealm channelRealm, ChannelJson channelJson) {
        ChannelRealm newChannelRealm = domainMapper.channelJsonAsRealm(channelRealm, channelJson);
        subscriber.onNext(newChannelRealm);
        dbRepository.putChannel(newChannelRealm).subscribeOn(scheduler).observeOn(postScheduler)
                .subscribe(subscriber::onCompleted, subscriber::onError);
    }
}
