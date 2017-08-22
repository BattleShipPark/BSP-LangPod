package com.battleshippark.bsp_langpod.data.downloader;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

interface DownloadService {
    @GET
    @Streaming
    Observable<Response<ResponseBody>> download(@Header(Downloader.HEADER_IDENTIFIER) String identifier, @Url String url);
}
