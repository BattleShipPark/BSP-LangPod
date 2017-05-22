package com.battleshippark.bsp_langpod.data.server;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

public interface ChannelService {
    @GET("/v1/totalList")
    Observable<EntireChannelListJson> queryEntireList();

    @GET
    Observable<MyChannelJson> queryUrl(@Url String url);
}
