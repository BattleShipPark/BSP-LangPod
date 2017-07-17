package com.battleshippark.bsp_langpod.data.download;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

interface DownloadService {
    @GET
    @Streaming
    Observable<Response<ResponseBody>> download(@Url String url);
}
