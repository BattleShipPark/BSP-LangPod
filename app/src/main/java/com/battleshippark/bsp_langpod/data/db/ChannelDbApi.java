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
    public Observable<List<ChannelRealm>> entireChannelList() {
        return Observable.create(subscriber -> {
            try {
                RealmQuery<ChannelRealm> query = realm.where(ChannelRealm.class);
                RealmResults<ChannelRealm> results = query.findAllSorted("order");
                subscriber.onNext(results);
                subscriber.onCompleted();
            } catch (Throwable t) {
                subscriber.onError(t);
            }
        });
    }

    @Override
    public Observable<List<ChannelRealm>> myChannelList() {
        return Observable.create(subscriber -> {
            RealmQuery<ChannelRealm> query = realm.where(ChannelRealm.class);
            RealmResults<ChannelRealm> results = query.equalTo(ChannelRealm.FIELD_SUBSCRIBED, true)
                    .findAllSorted(ChannelRealm.FIELD_ORDER);
            subscriber.onNext(results);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<ChannelRealm> channel(long id) {
        return Observable.create(subscriber -> {
            ChannelRealm channelRealm = realm.where(ChannelRealm.class).equalTo("id", id).findFirst();
            subscriber.onNext(channelRealm);
            subscriber.onCompleted();
        });
    }

    @Override
    public void putEntireChannelList(List<ChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException {
        realm.executeTransactionAsync(realm1 -> {
            realm1.delete(ChannelRealm.class);

            Stream.of(realmList).forEach(realm1::copyToRealm);
        });
    }

    @Override
    public void putChannel(ChannelRealm channelRealm) throws IllegalArgumentException, RealmMigrationNeededException {
        realm.executeTransactionAsync(realm1 -> {
            realm1.insertOrUpdate(channelRealm);
        });
    }
}
