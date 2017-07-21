package com.battleshippark.bsp_langpod.data.db;

import java.util.List;

import io.realm.exceptions.RealmMigrationNeededException;
import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<ChannelRealm>> entireChannelList();

    Observable<List<ChannelRealm>> myChannelList();

    // 한 건만 필요하지만 adapter에서 live update를 사용하기 위해 RealmResults를 반환하기 위해 List를 사용한다
    Observable<List<ChannelRealm>> channel(long id);

    void putEntireChannelList(List<ChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException;

    void putChannel(ChannelRealm channelRealm) throws IllegalArgumentException, RealmMigrationNeededException;

    void putEpisode(EpisodeRealm episodeRealm) throws IllegalArgumentException, RealmMigrationNeededException;

    Observable<Void> switchSubscribe(ChannelRealm channelRealm);
}
