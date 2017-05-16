package com.battleshippark.bsp_langpod.data.db;

import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;
import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import java.util.List;

import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<EntireChannelRealm>> entireChannelList();

    Observable<EntireChannelListJson> queryAll();

    Observable<MyChannelJson> query(int id);

    void putEntireChannelList(List<EntireChannelRealm> realmList);
}
