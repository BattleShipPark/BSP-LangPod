package com.battleshippark.bsp_langpod.data.server;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

interface ChannelService {
    @GET("/v1/entireList")
    Observable<EntireChannelListJson> queryEntireList();

    @GET
    Observable<ChannelJson> queryUrl(@Url String url);
}
