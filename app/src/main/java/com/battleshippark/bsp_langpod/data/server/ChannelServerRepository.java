package com.battleshippark.bsp_langpod.data.server;

import rx.Observable;

/**
 */

public interface ChannelServerRepository {
    Observable<EntireChannelListJson> entireChannelList();

    Observable<MyChannelJson> myChannel(String url);
}
