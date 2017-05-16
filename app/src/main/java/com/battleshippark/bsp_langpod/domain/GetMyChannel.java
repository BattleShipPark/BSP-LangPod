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

public class GetMyChannel implements UseCase<Void, MyChannelData> {
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
    public Observable<MyChannelData> execute(Void param) {
        return Observable.create(subscriber ->
                dbRepository.myChannel(0).subscribe(
                        myChannelRealm -> onDbLoaded(subscriber, myChannelRealm),
                        subscriber::onError, subscriber::onCompleted));
    }

    private void onDbLoaded(Subscriber<? super MyChannelData> subscriber, MyChannelRealm myChannelRealm) {
        subscriber.onNext(mapper.myChannelRealmAsData(myChannelRealm));

//        serverRepository.entireChannelList().subscribe(
//                entireChannelListJson -> onServerLoaded(subscriber, entireChannelListJson),
//                subscriber::onError, subscriber::onCompleted);
    }

/*    private void onServerLoaded(Subscriber<? super List<MyChannelData>> subscriber, EntireChannelListJson entireChannelListJson) {
        subscriber.onNext(mapper.asData(entireChannelListJson));

        try {
            dbRepository.putEntireChannelList(mapper.asRealm(entireChannelListJson));
        } catch (Exception e) {
            subscriber.onError(e);
        }

        subscriber.onCompleted();
    }*/
}
