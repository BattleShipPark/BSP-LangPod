package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.data.server.ChannelServerApi;
import com.battleshippark.bsp_langpod.data.server.rss.RssResponseMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 */

@Module
class ServerApiModule {
    @Provides
    @Singleton
    ChannelServerApi channelApi(AppPhase appPhase, RssResponseMapper mapper) {
        return new ChannelServerApi(appPhase, mapper);
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
