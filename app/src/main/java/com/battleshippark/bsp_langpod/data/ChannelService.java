package com.battleshippark.bsp_langpod.data;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

public interface ChannelService {
    @GET
    Observable<ChannelData> query(@Url String url);
}
