package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.RealmConfigurationFactory;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmConfiguration;

/**
 */

@Module
class DbApiModule {
    @Provides
    ChannelDbApi channelApi(RealmConfiguration configuration) {
        return new ChannelDbApi(configuration);
    }

    @Provides
    RealmConfiguration realmConfiguration() {
        return RealmConfigurationFactory.create();
    }
}
