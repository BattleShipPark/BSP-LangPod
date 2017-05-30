package com.battleshippark.bsp_langpod.data.server;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.server.rss.GsonAvTypeAdapterFactory;
import com.battleshippark.bsp_langpod.data.server.rss.RssConverterFactory;
import com.battleshippark.bsp_langpod.data.server.rss.RssResponseMapper;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 */

public class ChannelServerApi implements ChannelServerRepository {
    private final AppPhase appPhase;
    private final Converter.Factory rssConverterFactory, gsonConverterFactory;
    private final OkHttpClient client;

    @Inject
    public ChannelServerApi(AppPhase appPhase, RssResponseMapper mapper) {
        this.appPhase = appPhase;
        this.rssConverterFactory = RssConverterFactory.create(mapper);
        this.gsonConverterFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .registerTypeAdapterFactory(GsonAvTypeAdapterFactory.create())
                        .create());
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
    }

    @Override
    public Observable<EntireChannelListJson> entireChannelList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + appPhase.getServerDomain())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        ChannelService service = retrofit.create(ChannelService.class);
        return service.queryEntireList();
    }

    @Override
    public Observable<ChannelJson> myChannel(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + appPhase.getServerDomain())
                .addConverterFactory(rssConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        ChannelService service = retrofit.create(ChannelService.class);
        return service.queryUrl(url);
    }
}
