package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.server.ChannelServerApi;

import javax.inject.Singleton;

import dagger.Component;

/**
 */

@Component(modules = ServerApiModule.class)
@Singleton
public interface ServerApiGraph {
    ChannelServerApi channelApi();
}
