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
import rx.subjects.PublishSubject;

/**
 * https://blog.playmoweb.com/view-download-progress-on-android-using-retrofit2-and-okhttp3-83ed704cb968
 */
public class Downloader {
    static final String HEADER_IDENTIFIER = "header-identifier";
    private final AppPhase appPhase;
    private final OkHttpClient.Builder clientBuilder;

    public Downloader(AppPhase appPhase) {
        this.appPhase = appPhase;
        this.clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));
    }

    public Observable<File> download(String identifier, String url, String outputPath, PublishSubject<DownloadProgressParam> progressSubject) {
        OkHttpClient client = clientBuilder.addInterceptor(new DownloadInterceptor(progressSubject)).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + appPhase.getServerDomain())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        DownloadService service = retrofit.create(DownloadService.class);

        return service.download(identifier, url)
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
