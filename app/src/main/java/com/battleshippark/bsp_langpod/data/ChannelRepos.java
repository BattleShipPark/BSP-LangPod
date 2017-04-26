package com.battleshippark.bsp_langpod.data;

import com.battleshippark.bsp_langpod.data.rss.RssConverterFactory;
import com.battleshippark.bsp_langpod.data.rss.RssResponseMapper;

import javax.inject.Inject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 */

public class ChannelRepos implements ChannelInteractor<ChannelData> {
    private final RssResponseMapper mapper;

    @Inject
    public ChannelRepos(RssResponseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Observable<ChannelData> query(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.daum.net")
                .addConverterFactory(RssConverterFactory.create(mapper))
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ChannelService service = retrofit.create(ChannelService.class);
        return service.query(url);
    }
}
