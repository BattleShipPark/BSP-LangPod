package com.battleshippark.bsp_langpod.data.db;

import java.util.List;

import io.realm.exceptions.RealmMigrationNeededException;
import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<EntireChannelRealm>> entireChannelList();

    Observable<List<MyChannelRealm>> myChannelList();

    Observable<MyChannelRealm> myChannel(long id);

    void putEntireChannelList(List<EntireChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException;

    void putMyChannel(MyChannelRealm myChannelRealm) throws IllegalArgumentException, RealmMigrationNeededException;
}
