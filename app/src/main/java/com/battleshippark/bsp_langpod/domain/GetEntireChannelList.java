package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.EntireChannelListData;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 */

public class GetEntireChannelList implements UseCase<Void, EntireChannelListData> {
    private final ChannelServerRepository apiRepository;
    private final Executor executor;

    @Inject
    public GetEntireChannelList(ChannelServerRepository apiRepository, Executor executor) {
        this.apiRepository = apiRepository;
        this.executor = executor;
    }

    @Override
    public Observable<EntireChannelListData> execute(Void param) {
        return Observable.create(new MySubscriber());
    }

    private class MySubscriber implements Observable.OnSubscribe<EntireChannelListData> {
        @Override
        public void call(Subscriber<? super EntireChannelListData> subscriber) {
            try {
                apiRepository.entireChannelList().subscribe(subscriber::onNext);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }
    }
}
