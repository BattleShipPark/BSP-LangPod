package com.battleshippark.bsp_langpod.data.db;

import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;
import com.battleshippark.bsp_langpod.data.server.MyChannelData;

import java.util.List;

import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<EntireChannelRealm>> entireChannelList();

    Observable<EntireChannelListData> queryAll();

    Observable<MyChannelData> query(int id);

    void putEntireChannelList(List<EntireChannelRealm> realmList);
}
