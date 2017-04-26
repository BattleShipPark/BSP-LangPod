package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.ChannelRepos;
import com.battleshippark.bsp_langpod.data.rss.RssResponseMapper;

import dagger.Module;
import dagger.Provides;

/**
 */

@Module
public class ChannelReposModule {
    @Provides
    ChannelRepos provideChannelRepos(RssResponseMapper mapper) {
        return new ChannelRepos(mapper);
    }

    @Provides
    RssResponseMapper provideRssResponseMapper() {
        return new RssResponseMapper();
    }
}
