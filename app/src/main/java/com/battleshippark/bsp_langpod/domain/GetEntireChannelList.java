package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;

/**
 */

public class GetEntireChannelList implements UseCase<Void, EntireChannelListJson> {
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
    public Observable<EntireChannelListJson> execute(Void param) {
        return Observable.create(subscriber -> {
            try {
                List<EntireChannelRealm> dbEntireChannelReamList = dbRepository.entireChannelList().toBlocking().single();
                subscriber.onNext(mapper.asData(dbEntireChannelReamList));

                EntireChannelListJson serverEntireChannelListJson = apiRepository.entireChannelList().toBlocking().single();

                subscriber.onNext(serverEntireChannelListJson);

                dbRepository.putEntireChannelList(mapper.asRealm(serverEntireChannelListJson));

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
