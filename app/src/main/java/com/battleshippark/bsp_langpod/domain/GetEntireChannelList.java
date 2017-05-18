package com.battleshippark.bsp_langpod.domain;

import android.util.Log;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 */

public class GetEntireChannelList implements UseCase<Void, List<EntireChannelData>> {
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
    public Observable<List<EntireChannelData>> execute(Void param) {
        return Observable.create(subscriber ->
                dbRepository.entireChannelList().subscribe(
                        entireChannelRealmList -> onDbLoaded(subscriber, entireChannelRealmList),
                        subscriber::onError));
    }

    private void onDbLoaded(Subscriber<? super List<EntireChannelData>> subscriber, List<EntireChannelRealm> entireChannelRealmList) {
        try {
            subscriber.onNext(domainMapper.asData(entireChannelRealmList));

            serverRepository.entireChannelList().subscribeOn(scheduler).observeOn(postScheduler).subscribe(
                    this::onServerLoaded,
                    subscriber::onError, subscriber::onCompleted);
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private void onServerLoaded(EntireChannelListJson entireChannelListJson) {
        dbRepository.putEntireChannelList(domainMapper.entireChannelListJsonAsRealm(entireChannelListJson));
    }
}
