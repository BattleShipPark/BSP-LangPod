package com.battleshippark.bsp_langpod.data.downloader;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import rx.subjects.PublishSubject;

/**
 */

class DownloadInterceptor implements Interceptor {
    private PublishSubject<DownloadProgressParam> downloadProgress;

    DownloadInterceptor(PublishSubject<DownloadProgressParam> downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        Response.Builder builder = originalResponse.newBuilder();

        String downloadIdentifier = originalResponse.request().header(Downloader.HEADER_IDENTIFIER);
        builder.body(new DownloadResponseBody(downloadIdentifier, originalResponse.body(), downloadProgress));

        return builder.build();

    }
}
