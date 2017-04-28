package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.data.ChannelApi;
import com.battleshippark.bsp_langpod.data.ChannelListApi;
import com.battleshippark.bsp_langpod.data.rss.RssResponseMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 */

@Module
class ServerApiModule {
    @Provides
    @Singleton
    ChannelListApi channelListApi(AppPhase appPhase) {
        return new ChannelListApi(appPhase);
    }

    @Provides
    @Singleton
    ChannelApi channelApi(RssResponseMapper mapper) {
        return new ChannelApi(mapper);
    }

    @Provides
    @Singleton
    RssResponseMapper rssResponseMapper() {
        return new RssResponseMapper();
    }

    @Provides
    @Singleton
    AppPhase appPhase() {
        return new AppPhase(BuildConfig.DEBUG);
    }
}
