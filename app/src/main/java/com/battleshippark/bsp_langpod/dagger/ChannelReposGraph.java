package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.ChannelRepos;

import dagger.Component;

/**
 */

@Component(modules = ChannelReposModule.class)
public interface ChannelReposGraph {
    ChannelRepos createRepos();
}
