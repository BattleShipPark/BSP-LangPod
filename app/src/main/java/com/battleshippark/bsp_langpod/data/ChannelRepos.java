package com.battleshippark.bsp_langpod.data;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;

/**
 */

public class ChannelRepos implements ChannelInteractor<String> {
    @Override
    public Observable<String> query(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.daum.net")
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ChannelService service = retrofit.create(ChannelService.class);
        return service.query(url);
    }
}
