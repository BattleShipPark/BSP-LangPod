package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.data.ChannelListRepos;
import com.battleshippark.bsp_langpod.data.ChannelRepos;
import com.battleshippark.bsp_langpod.data.rss.RssResponseMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 */

@Module
class ChannelReposModule {
    @Provides
    @Singleton
    ChannelListRepos provideChannelListRepos(AppPhase appPhase) {
        return new ChannelListRepos(appPhase);
    }

    @Provides
    @Singleton
    ChannelRepos provideChannelRepos(RssResponseMapper mapper) {
        return new ChannelRepos(mapper);
    }

    @Provides
    @Singleton
    RssResponseMapper provideRssResponseMapper() {
        return new RssResponseMapper();
    }

    @Provides
    @Singleton
    AppPhase provideAppPhase() {
        return new AppPhase(BuildConfig.DEBUG);
    }
}
