package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 */

@Module
class DbApiModule {
    @Provides
    ChannelDbApi channelApi(Lazy<Realm> realm) {
        return new ChannelDbApi(realm);
    }

    @Provides
    Realm realm() {
        return Realm.getDefaultInstance();
    }
}
