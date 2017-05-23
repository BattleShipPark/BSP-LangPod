package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

public class GetMyChannel implements UseCase<Long, ChannelRealm> {
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
    public Observable<ChannelRealm> execute(Long id) {
        return Observable.create(subscriber ->
                dbRepository.channel(id).subscribe(
                        channelRealm -> onDbLoaded(subscriber, channelRealm),
                        subscriber::onError));
    }

    private void onDbLoaded(Subscriber<? super ChannelRealm> subscriber, ChannelRealm channelRealm) {
        subscriber.onNext(channelRealm);

        serverRepository.myChannel(channelRealm.getUrl()).subscribeOn(scheduler).observeOn(postScheduler)
                .subscribe(myChannelJson -> onServerLoaded(channelRealm, myChannelJson),
                        subscriber::onError, subscriber::onCompleted);
    }

    private void onServerLoaded(ChannelRealm channelRealm, MyChannelJson myChannelJson) {
        dbRepository.putMyChannel(domainMapper.myChannelJsonAsRealm(channelRealm, myChannelJson));
    }
}
