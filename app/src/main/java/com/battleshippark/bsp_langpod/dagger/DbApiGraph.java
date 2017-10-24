package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.DownloadDbApi;

import javax.inject.Singleton;

import dagger.Component;

/**
 */

@Singleton
@Component(modules = DbApiModule.class)
public interface DbApiGraph {
    ChannelDbApi channelApi();

    DownloadDbApi downloadApi();
}
