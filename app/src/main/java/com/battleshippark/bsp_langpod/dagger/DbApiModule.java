package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 */

@Module
class DbApiModule {
    @Provides
    @Singleton
    ChannelDbApi channelApi() {
        return new ChannelDbApi();
    }
}
