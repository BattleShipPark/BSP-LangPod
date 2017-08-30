package com.battleshippark.bsp_langpod.data.db;

import java.util.List;

import rx.Completable;
import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<ChannelRealm>> entireChannelList();

    Observable<List<ChannelRealm>> myChannelList();

    Observable<ChannelRealm> channel(long id);

    Observable<ChannelRealm> channelWithEpisodeId(long episodeId);

    Completable putEntireChannelList(List<ChannelRealm> realmList);

    Completable putChannel(ChannelRealm channelRealm);

    Completable putEpisode(EpisodeRealm episodeRealm);

    Completable switchSubscribe(ChannelRealm channelRealm);
}
