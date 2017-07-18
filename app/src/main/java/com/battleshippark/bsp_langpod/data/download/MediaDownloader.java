package com.battleshippark.bsp_langpod.data.download;

import android.content.Context;
import android.os.Environment;

import com.battleshippark.bsp_langpod.AppPhase;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Scheduler;

/**
 */
public class MediaDownloader {
    private final Downloader downloader;
    private final Context context;

    public MediaDownloader(Context context, Scheduler scheduler, Scheduler postScheduler, AppPhase appPhase, DownloadListener downloadListener) {
        this.context = context;
        this.downloader = new Downloader(scheduler, postScheduler, appPhase, downloadListener);
    }

    public Observable<File> download(String url, String outputFolder, String outputFile) {
        return downloader.download(url, new File(context.getExternalFilesDir(outputFolder), outputFile).getAbsolutePath());
    }
}
