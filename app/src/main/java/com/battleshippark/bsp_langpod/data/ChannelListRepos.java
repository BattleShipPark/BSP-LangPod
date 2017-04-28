package com.battleshippark.bsp_langpod.data;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.rss.GsonAvTypeAdapterFactory;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 */

public class ChannelListRepos implements ChannelInteractor<ChannelListData> {
    private final AppPhase appPhase;
    private final GsonConverterFactory gsonConverterFactory;

    @Inject
    public ChannelListRepos(AppPhase appPhase) {
        this.appPhase = appPhase;
        this.gsonConverterFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .registerTypeAdapterFactory(GsonAvTypeAdapterFactory.create())
                        .create());
    }

    @Override
    public Observable<ChannelListData> query(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + appPhase.getServerDomain())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ChannelListService service = retrofit.create(ChannelListService.class);
        return service.query();
    }
}
