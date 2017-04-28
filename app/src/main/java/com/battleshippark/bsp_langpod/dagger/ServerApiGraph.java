package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.ChannelApi;
import com.battleshippark.bsp_langpod.data.ChannelListApi;

import javax.inject.Singleton;

import dagger.Component;

/**
 */

@Component(modules = ServerApiModule.class)
@Singleton
public interface ServerApiGraph {
    ChannelApi channelApi();

    ChannelListApi channelListApi();
}
