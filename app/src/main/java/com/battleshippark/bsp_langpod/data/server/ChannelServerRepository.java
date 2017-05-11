package com.battleshippark.bsp_langpod.data.server;

import rx.Observable;

/**
 */

public interface ChannelServerRepository {
    Observable<EntireChannelListData> entireChannelList();

    Observable<MyChannelData> channel(String url);
}
