package com.battleshippark.bsp_langpod.data.server;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

public interface ChannelService {
    @GET("/totalList")
    Observable<EntireChannelListData> queryTotalList();

    @GET
    Observable<MyChannelData> queryUrl(@Url String url);
}
