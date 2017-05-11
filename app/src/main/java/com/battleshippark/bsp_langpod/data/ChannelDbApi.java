package com.battleshippark.bsp_langpod.data;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
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
    public Observable<EntireChannelListData> queryAll() {
        return null;
    }

    @Override
    public Observable<MyChannelData> query(int id) {
        return null;
    }
}
