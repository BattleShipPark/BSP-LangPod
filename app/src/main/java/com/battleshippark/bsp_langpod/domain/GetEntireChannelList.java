package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 */

public class GetEntireChannelList implements UseCase<Void, EntireChannelListData> {
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
    public Observable<EntireChannelListData> execute(Void param) {
        return Observable.create(subscriber -> {
            try {
                dbRepository.entireChannelList().subscribe(new DbSubscriber(subscriber));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    class DbSubscriber extends Subscriber<List<EntireChannelRealm>> {
        private Subscriber<? super EntireChannelListData> subscriber;

        DbSubscriber(Subscriber<? super EntireChannelListData> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onCompleted() {
            apiRepository.entireChannelList().subscribe(new ServerSubscriber(subscriber));
        }

        @Override
        public void onError(Throwable e) {
            subscriber.onError(e);
        }

        @Override
        public void onNext(List<EntireChannelRealm> entireChannelRealmList) {
            subscriber.onNext(mapper.asData(entireChannelRealmList));
        }
    }

    class ServerSubscriber extends Subscriber<EntireChannelListData> {
        private Subscriber<? super EntireChannelListData> subscriber;

        ServerSubscriber(Subscriber<? super EntireChannelListData> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            subscriber.onError(e);
        }

        @Override
        public void onNext(EntireChannelListData entireChannelListData) {
            subscriber.onNext(entireChannelListData);
        }
    }
}
