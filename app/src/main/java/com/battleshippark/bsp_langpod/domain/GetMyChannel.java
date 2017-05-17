package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 */

public class GetMyChannel implements UseCase<MyChannelData, MyChannelData> {
    private final ChannelDbRepository dbRepository;
    private final ChannelServerRepository serverRepository;
    private final Executor executor;
    private final RealmMapper mapper;

    @Inject
    public GetMyChannel(ChannelDbRepository dbRepository, ChannelServerRepository serverRepository, Executor executor, RealmMapper mapper) {
        this.dbRepository = dbRepository;
        this.serverRepository = serverRepository;
        this.executor = executor;
        this.mapper = mapper;
    }

    @Override
    public Observable<MyChannelData> execute(MyChannelData localMyChannelData) {
        return Observable.create(subscriber ->
                dbRepository.myChannel(localMyChannelData.id()).subscribe(
                        myChannelRealm -> onDbLoaded(subscriber, localMyChannelData, myChannelRealm),
                        subscriber::onError, subscriber::onCompleted));
    }

    private void onDbLoaded(Subscriber<? super MyChannelData> subscriber, MyChannelData localMyChannelData, MyChannelRealm myChannelRealm) {
        subscriber.onNext(mapper.myChannelRealmAsData(myChannelRealm));

        serverRepository.myChannel(localMyChannelData.url()).subscribe(
                myChannelJson -> onServerLoaded(subscriber, localMyChannelData, myChannelJson),
                subscriber::onError, subscriber::onCompleted);
    }

    private void onServerLoaded(Subscriber<? super MyChannelData> subscriber, MyChannelData localMyChannelData, MyChannelJson myChannelJson) {
        subscriber.onNext(mapper.myChannelJsonAsData(localMyChannelData.url(), myChannelJson));

        try {
            dbRepository.putMyChannel(mapper.myChannelJsonAsRealm(localMyChannelData.id(),
                    localMyChannelData.order(), localMyChannelData.url(),
                    myChannelJson));
        } catch (Exception e) {
            subscriber.onError(e);
        }

        subscriber.onCompleted();
    }
}
