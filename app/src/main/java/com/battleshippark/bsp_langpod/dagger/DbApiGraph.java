package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.server.ChannelServerApi;

import javax.inject.Singleton;

import dagger.Component;

/**
 */

@Component(modules = DbApiModule.class)
@Singleton
public interface DbApiGraph {
    ChannelDbApi channelApi();
}
