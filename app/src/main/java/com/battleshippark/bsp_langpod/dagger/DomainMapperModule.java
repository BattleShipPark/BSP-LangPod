package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.data.db.RealmHelperImpl;
import com.battleshippark.bsp_langpod.domain.DomainMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 */

@Module
class DomainMapperModule {
    @Provides
    @Singleton
    DomainMapper domainMapper() {
        return new DomainMapper(new RealmHelperImpl(Realm.getDefaultInstance()));
    }
}
