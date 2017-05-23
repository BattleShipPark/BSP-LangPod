package com.battleshippark.bsp_langpod.data.server;

import rx.Observable;

/**
 */

public interface ChannelServerRepository {
    Observable<EntireChannelListJson> entireChannelList();

    Observable<ChannelJson> myChannel(String url);
}
