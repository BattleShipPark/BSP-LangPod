package com.battleshippark.bsp_langpod.data;

import java.util.List;

import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<EntireChannelRealm>> entireChannelList();

    Observable<EntireChannelListData> queryAll();

    Observable<MyChannelData> query(int id);
}
