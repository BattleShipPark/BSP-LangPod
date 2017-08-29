package com.battleshippark.bsp_langpod.data.db;

import android.support.annotation.VisibleForTesting;

import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Completable;
import rx.Observable;

/**
 */

public class ChannelDbApi implements ChannelDbRepository {
    private final Lazy<Realm> realm;

    @VisibleForTesting
    public ChannelDbApi() {
        this(Realm.getDefaultInstance());
    }

    @VisibleForTesting
    public ChannelDbApi(Realm realm) {
        this.realm = () -> realm;
    }

    @Inject
    public ChannelDbApi(Lazy<Realm> realm) {
        this.realm = realm;
    }

    @Override
    public Observable<List<ChannelRealm>> entireChannelList() {
        return Observable.create(subscriber -> {
            try {
                RealmQuery<ChannelRealm> query = realm.get().where(ChannelRealm.class);
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
            try {
                RealmQuery<ChannelRealm> query = realm.get().where(ChannelRealm.class);
                RealmResults<ChannelRealm> results = query.equalTo(ChannelRealm.FIELD_SUBSCRIBED, true)
                        .findAllSorted(ChannelRealm.FIELD_ORDER);
                subscriber.onNext(realm.get().copyFromRealm(results));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<ChannelRealm>> channel(long id) {
        return Observable.create(subscriber -> {
            try {
                RealmResults<ChannelRealm> results = realm.get().where(ChannelRealm.class).equalTo("id", id).findAll();
                subscriber.onNext(results);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<ChannelRealm> channelWithEpisodeId(long episodeId) {
        return Observable.create(subscriber -> {
            try {
                RealmResults<ChannelRealm> results = realm.get().where(ChannelRealm.class).equalTo("episodes.id", episodeId).findAll();
                subscriber.onNext(realm.get().copyFromRealm(results.get(0)));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable putEntireChannelList(List<ChannelRealm> realmList) {
        return Completable.create(subscriber -> {
            realm.get().beginTransaction();
            try {
                realm.get().delete(ChannelRealm.class);
                Stream.of(realmList).forEach(realm.get()::insertOrUpdate);

                realm.get().commitTransaction();
                subscriber.onCompleted();
            } catch (Exception e) {
                realm.get().cancelTransaction();
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable putChannel(ChannelRealm channelRealm) {
        return Completable.create(subscriber -> {
            realm.get().beginTransaction();
            try {
                realm.get().insertOrUpdate(channelRealm);

                realm.get().commitTransaction();
                subscriber.onCompleted();
            } catch (Exception e) {
                realm.get().cancelTransaction();
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable putEpisode(EpisodeRealm episodeRealm) {
        return Completable.create(subscriber -> {
            realm.get().beginTransaction();
            try {
                realm.get().insertOrUpdate(episodeRealm);
                realm.get().commitTransaction();
                subscriber.onCompleted();
            } catch (Exception e) {
                realm.get().cancelTransaction();
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable switchSubscribe(ChannelRealm channelRealm) {
        return Completable.create(subscriber -> {
            long id = channelRealm.getId();
            boolean value = !channelRealm.isSubscribed();
            realm.get().beginTransaction();
            try {
                ChannelRealm newChannelRealm = realm.get().where(ChannelRealm.class).equalTo(ChannelRealm.FIELD_ID, id).findFirst();
                newChannelRealm.setSubscribed(value);
                realm.get().commitTransaction();
                subscriber.onCompleted();
            } catch (Exception e) {
                realm.get().cancelTransaction();
                subscriber.onError(e);
            }
        });
    }
}
