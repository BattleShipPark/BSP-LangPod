package com.battleshippark.bsp_langpod.data.db;

import com.annimon.stream.Stream;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;
import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import rx.Observable;

/**
 */

public class ChannelDbApi implements ChannelDbRepository {
    private final Realm realm;

    @Inject
    public ChannelDbApi(Realm realm) {
        this.realm = realm;
    }

    @Override
    public Observable<List<EntireChannelRealm>> entireChannelList() {
        RealmQuery<EntireChannelRealm> query = realm.where(EntireChannelRealm.class);
        RealmResults<EntireChannelRealm> results = query.findAllSorted("id");
        return Observable.just(results);
    }

    @Override
    public Observable<EntireChannelListJson> queryAll() {
        return null;
    }

    @Override
    public Observable<MyChannelJson> query(int id) {
        return null;
    }

    @Override
    public void putEntireChannelList(List<EntireChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException {
        realm.executeTransaction(realm1 -> {
            realm1.delete(EntireChannelRealm.class);

            Stream.of(realmList).forEach(realm1::copyToRealm);
        });
    }
}
