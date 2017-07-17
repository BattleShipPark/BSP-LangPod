package com.battleshippark.bsp_langpod.data.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 */

class DownloadInterceptor implements Interceptor {
    private DownloadListener downloadListener;

    DownloadInterceptor(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        Response.Builder builder = originalResponse.newBuilder();

        builder.body(new DownloadResponseBody(originalResponse.body(), downloadListener));

        return builder.build();

    }
}
