package com.battleshippark.bsp_langpod.data;

import java.util.List;

import rx.Observable;

/**
 */

public interface ChannelDbRepository {
    Observable<List<EntireChannelRealm>> entireChannel();

    Observable<ChannelListData> queryAll();

    Observable<ChannelData> query(int id);
}
