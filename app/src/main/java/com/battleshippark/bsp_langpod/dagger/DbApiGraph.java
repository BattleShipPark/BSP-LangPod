package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;

import dagger.Component;

/**
 */

@Component(modules = DbApiModule.class)
public interface DbApiGraph {
    ChannelDbApi channelApi();
}
