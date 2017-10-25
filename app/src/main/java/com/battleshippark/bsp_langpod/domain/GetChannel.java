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

public class GetChannel implements UseCase<GetChannel.Param, ChannelRealm> {
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
    public Observable<ChannelRealm> execute(Param param) {
        return Observable.create(subscriber ->
                dbRepository.channel(param.channelId).subscribeOn(scheduler).observeOn(postScheduler)
                        .subscribe(channelRealm -> onDbLoaded(param, subscriber, channelRealm),
                                throwable -> subscriber.onError(new GetChannelThrowable(throwable, Type.ONLY_DB))));
    }

    private void onDbLoaded(Param param, Subscriber<? super ChannelRealm> subscriber, ChannelRealm channelRealm) {
        subscriber.onNext(channelRealm);

        if (param.type == Type.ONLY_DB) {
            subscriber.onCompleted();
        } else {
            serverRepository.myChannel(channelRealm.getUrl()).subscribeOn(scheduler).observeOn(postScheduler)
                    .subscribe(myChannelJson -> onServerLoaded(subscriber, channelRealm, myChannelJson),
                            throwable -> subscriber.onError(new GetChannelThrowable(throwable, Type.DB_AND_SERVER)),
                            subscriber::onCompleted);
        }
    }

    private void onServerLoaded(Subscriber<? super ChannelRealm> subscriber, ChannelRealm channelRealm, ChannelJson channelJson) {
        ChannelRealm newChannelRealm = domainMapper.channelJsonAsRealm(channelRealm, channelJson);
        subscriber.onNext(newChannelRealm);
        dbRepository.putChannel(newChannelRealm).subscribeOn(scheduler).observeOn(postScheduler)
                .subscribe(subscriber::onCompleted, throwable -> subscriber.onError(new GetChannelThrowable(throwable, Type.DB_AND_SERVER)));
    }

    public static class Param {
        final long channelId;
        final Type type;

        public Param(long channelId, Type type) {
            this.channelId = channelId;
            this.type = type;
        }
    }

    public enum Type {
        ONLY_DB, DB_AND_SERVER
    }

    public static class GetChannelThrowable extends Throwable {
        private final Type type;

        GetChannelThrowable(Throwable t, Type type) {
            super(t);
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }
}
