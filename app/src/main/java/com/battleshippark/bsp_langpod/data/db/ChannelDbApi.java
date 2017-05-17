package com.battleshippark.bsp_langpod.data.db;

import com.annimon.stream.Stream;

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
    public Observable<List<MyChannelRealm>> myChannelList() {
        return Observable.create(subscriber -> {
            RealmQuery<MyChannelRealm> query = realm.where(MyChannelRealm.class);
            RealmResults<MyChannelRealm> results = query.findAllSorted("order");
            subscriber.onNext(results);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<MyChannelRealm> myChannel(long id) {
        return Observable.create(subscriber -> {
            MyChannelRealm myChannelRealm = realm.where(MyChannelRealm.class).equalTo("id", id).findFirst();
            subscriber.onNext(myChannelRealm);
            subscriber.onCompleted();
        });
    }

    @Override
    public void putEntireChannelList(List<EntireChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException {
        realm.executeTransaction(realm1 -> {
            realm1.delete(EntireChannelRealm.class);

            Stream.of(realmList).forEach(realm1::copyToRealm);
        });
    }

    @Override
    public void putMyChannel(MyChannelRealm myChannelRealm) throws IllegalArgumentException, RealmMigrationNeededException {
        realm.executeTransaction(realm1 -> {
            realm1.delete(MyChannelRealm.class);

            realm1.copyToRealm(myChannelRealm);
        });
    }
}
