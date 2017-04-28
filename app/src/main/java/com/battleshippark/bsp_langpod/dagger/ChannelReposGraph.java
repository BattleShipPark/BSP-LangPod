package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.ChannelListRepos;
import com.battleshippark.bsp_langpod.data.ChannelRepos;

import javax.inject.Singleton;

import dagger.Component;

/**
 */

@Component(modules = ChannelReposModule.class)
@Singleton
public interface ChannelReposGraph {
    ChannelRepos createRepos();

    ChannelListRepos createListRepos();
}
