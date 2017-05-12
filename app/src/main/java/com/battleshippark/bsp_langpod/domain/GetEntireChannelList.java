package com.battleshippark.bsp_langpod.domain;

import android.support.annotation.VisibleForTesting;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelData;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;

import java.util.ArrayList;
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
                List<EntireChannelRealm> dbEntireChannelReamList = dbRepository.entireChannelList().toBlocking().single();
                subscriber.onNext(mapper.asData(dbEntireChannelReamList));

                EntireChannelListData serverEntireChannelListData = apiRepository.entireChannelList().toBlocking().single();

                List<EntireChannelRealm> merged = merge(dbEntireChannelReamList, serverEntireChannelListData);
//                dbRepository.putEntireChannelList(merged);

                subscriber.onNext(mapper.asData(merged));

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @VisibleForTesting
    List<EntireChannelRealm> merge(List<EntireChannelRealm> dbEntireChannelReamList, EntireChannelListData serverEntireChannelListData) {
        List<EntireChannelRealm> entireChannelRealmList = new ArrayList<>(dbEntireChannelReamList);

        List<EntireChannelRealm> addedList = Stream.of(serverEntireChannelListData.items())
                .filter(entireChannelData ->
                        Stream.of(dbEntireChannelReamList)
                                .noneMatch(entireChannelRealm -> equals(entireChannelRealm, entireChannelData))
                ).map(mapper::asRealm).collect(Collectors.toList());
        entireChannelRealmList.addAll(addedList);

        return entireChannelRealmList;
    }

    private boolean equals(EntireChannelRealm entireChannelRealm, EntireChannelData entireChannelData) {
        return entireChannelRealm.getTitle().equals(entireChannelData.title());
    }
}
