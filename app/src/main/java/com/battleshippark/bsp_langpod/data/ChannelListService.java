package com.battleshippark.bsp_langpod.data;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

public interface ChannelListService {
    @GET("/totalList")
    Observable<ChannelListData> query();
}
