package com.battleshippark.bsp_langpod.data.db;

import java.util.List;

import io.realm.exceptions.RealmMigrationNeededException;
import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<ChannelRealm>> entireChannelList();

    Observable<List<ChannelRealm>> myChannelList();

    Observable<ChannelRealm> channel(long id);

    void putEntireChannelList(List<ChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException;

    void putMyChannel(ChannelRealm channelRealm) throws IllegalArgumentException, RealmMigrationNeededException;
}
