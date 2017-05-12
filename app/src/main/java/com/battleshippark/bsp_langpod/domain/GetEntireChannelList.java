package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;

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
                List<EntireChannelRealm> dbEntireChannelReamList = dbRepository.entireChannelList().toBlocking().single();
                subscriber.onNext(mapper.asData(dbEntireChannelReamList));

                EntireChannelListData serverEntireChannelListData = apiRepository.entireChannelList().toBlocking().single();

                subscriber.onNext(serverEntireChannelListData);

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
