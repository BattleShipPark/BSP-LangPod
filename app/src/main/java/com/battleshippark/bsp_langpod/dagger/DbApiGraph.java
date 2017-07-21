package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;

import javax.inject.Singleton;

import dagger.Component;

/**
 */

@Component(modules = DbApiModule.class)
@Singleton
public interface DbApiGraph {
    ChannelDbApi channelApi();
}
