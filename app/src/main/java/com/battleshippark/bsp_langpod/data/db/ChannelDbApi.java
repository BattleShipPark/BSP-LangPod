package com.battleshippark.bsp_langpod.data.db;

import com.annimon.stream.Stream;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 */

public class ChannelDbApi implements ChannelDbRepository {
    @Inject
    public ChannelDbApi() {
    }

    @Override
    public Observable<List<ChannelRealm>> entireChannelList() {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmQuery<ChannelRealm> query = realm.where(ChannelRealm.class);
                RealmResults<ChannelRealm> results = query.findAllSorted("order");
                subscriber.onNext(results);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<ChannelRealm>> myChannelList() {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmQuery<ChannelRealm> query = realm.where(ChannelRealm.class);
                RealmResults<ChannelRealm> results = query.equalTo(ChannelRealm.FIELD_SUBSCRIBED, true)
                        .findAllSorted(ChannelRealm.FIELD_ORDER);
                subscriber.onNext(results);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ChannelRealm>> channel(long id) {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmResults<ChannelRealm> results = realm.where(ChannelRealm.class).equalTo("id", id).findAll();
                subscriber.onNext(results);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void putEntireChannelList(List<ChannelRealm> realmList) throws IllegalArgumentException, RealmMigrationNeededException {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(realm1 -> {
                realm1.delete(ChannelRealm.class);

                Stream.of(realmList).forEach(realm1::insertOrUpdate);
            });
        }
    }

    @Override
    public void putChannel(ChannelRealm channelRealm) throws
            IllegalArgumentException, RealmMigrationNeededException {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(realm1 -> {
                realm1.insertOrUpdate(channelRealm);
            });
        }
    }

    @Override
    public Observable<Void> putEpisode(EpisodeRealm episodeRealm) {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.beginTransaction();
                try {
                    realm.insertOrUpdate(episodeRealm);
                    realm.commitTransaction();
                    subscriber.onCompleted();
                } catch (Exception e) {
                    realm.cancelTransaction();
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> switchSubscribe(ChannelRealm channelRealm) {
        PublishSubject<Void> subject = PublishSubject.create();
        long id = channelRealm.getId();
        boolean value = !channelRealm.isSubscribed();
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(
                    realm1 -> {
                        ChannelRealm newChannelRealm = realm1.where(ChannelRealm.class).equalTo(ChannelRealm.FIELD_ID, id).findFirst();
                        newChannelRealm.setSubscribed(value);
                    },
                    () -> subject.onNext(null),
                    subject::onError);
            return subject;
        }
    }
}
