package com.battleshippark.bsp_langpod.data.download;

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
 * https://blog.playmoweb.com/view-download-progress-on-android-using-retrofit2-and-okhttp3-83ed704cb968
 */
public class Downloader {
    private final Scheduler scheduler;
    private final Scheduler postScheduler;
    private final AppPhase appPhase;
    private final OkHttpClient client;

    Downloader(Scheduler scheduler, Scheduler postScheduler, AppPhase appPhase, DownloadListener downloadListener) {
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
        this.appPhase = appPhase;
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(new DownloadInterceptor(downloadListener))
                .build();
    }

    public Observable<File> download(String url, String outputPath) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + appPhase.getServerDomain())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        DownloadService service = retrofit.create(DownloadService.class);

        return service.download(url)
                .flatMap(response -> {
                    try {
                        File file = new File(outputPath);
                        try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
                            sink.writeAll(response.body().source());
                        }

                        return Observable.just(file);
                    } catch (IOException e) {
                        return Observable.error(e);
                    }
                });
    }
}
