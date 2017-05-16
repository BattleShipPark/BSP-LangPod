package com.battleshippark.bsp_langpod.data.server;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.server.rss.GsonAvTypeAdapterFactory;
import com.battleshippark.bsp_langpod.data.server.rss.RssConverterFactory;
import com.battleshippark.bsp_langpod.data.server.rss.RssResponseMapper;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

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

    @Inject
    public ChannelServerApi(AppPhase appPhase, RssResponseMapper mapper) {
        this.appPhase = appPhase;
        this.rssConverterFactory = RssConverterFactory.create(mapper);
        this.gsonConverterFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .registerTypeAdapterFactory(GsonAvTypeAdapterFactory.create())
                        .create());
    }

    @Override
    public Observable<EntireChannelListJson> entireChannelList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + appPhase.getServerDomain())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ChannelService service = retrofit.create(ChannelService.class);
        return service.queryTotalList();
    }

    @Override
    public Observable<MyChannelJson> channel(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + appPhase.getServerDomain())
                .addConverterFactory(rssConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ChannelService service = retrofit.create(ChannelService.class);
        return service.queryUrl(url);
    }
}
