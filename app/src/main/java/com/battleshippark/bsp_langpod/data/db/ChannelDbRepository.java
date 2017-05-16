package com.battleshippark.bsp_langpod.data.db;

import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import java.util.List;

import io.realm.exceptions.RealmMigrationNeededException;
import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<EntireChannelRealm>> entireChannelList();

    Observable<List<MyChannelRealm>> myChannelList();

    Observable<MyChannelJson> query(int id);

    void putEntireChannelList(List<EntireChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException;
}
