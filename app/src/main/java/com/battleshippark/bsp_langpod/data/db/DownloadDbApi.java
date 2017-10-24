package com.battleshippark.bsp_langpod.data.db;

import android.support.annotation.VisibleForTesting;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Completable;
import rx.Observable;

/**
 */

public class DownloadDbApi implements DownloadDbRepository {
    private final RealmConfiguration realmConfiguration;

    @VisibleForTesting
    DownloadDbApi() {
        this.realmConfiguration = RealmConfigurationFactory.createTest();
    }

    @Inject
    public DownloadDbApi(RealmConfiguration realmConfiguration) {
        this.realmConfiguration = realmConfiguration;
    }

    @Override
    public Observable<List<DownloadRealm>> all() {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getInstance(realmConfiguration)) {
                RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class);
                RealmResults<DownloadRealm> results = query.findAllSorted(DownloadRealm.FIELD_DOWNLOAD_DATE);
                subscriber.onNext(realm.copyFromRealm(results));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<DownloadRealm>> getNotDownloaded() {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getInstance(realmConfiguration)) {
                RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class)
                        .equalTo(DownloadRealm.FIELD_DOWNLOAD_STATE, DownloadRealm.DownloadState.NOT_DOWNLOADED.name());
                RealmResults<DownloadRealm> results = query.findAllSorted(DownloadRealm.FIELD_DOWNLOAD_DATE);
                subscriber.onNext(realm.copyFromRealm(results));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable insert(DownloadRealm downloadRealm) {
        return Completable.create(subscriber -> {
            try (Realm realm = Realm.getInstance(realmConfiguration)) {
                realm.executeTransaction(realm1 -> realm1.insert(downloadRealm));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable delete(DownloadRealm downloadRealm) {
        return Completable.create(subscriber -> {
            try (Realm realm = Realm.getInstance(realmConfiguration)) {
                RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class)
                        .equalTo(DownloadRealm.FIELD_DOWNLOAD_STATE, DownloadRealm.DownloadState.NOT_DOWNLOADED.name());
                RealmResults<DownloadRealm> results = query.findAllSorted(DownloadRealm.FIELD_DOWNLOAD_DATE);
                results.deleteAllFromRealm();
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Completable update(DownloadRealm downloadRealm) {
        return Completable.create(subscriber -> {
            try (Realm realm = Realm.getInstance(realmConfiguration)) {
                realm.executeTransaction(realm1 -> {
                    RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class)
                            .equalTo(DownloadRealm.FIELD_EPISODE_REALM + "." + EpisodeRealm.FIELD_ID, downloadRealm.getEpisodeRealm().getId());
                    DownloadRealm queriedDownloadRealm = query.findFirst();

                    queriedDownloadRealm.setEpisodeRealm(downloadRealm.getEpisodeRealm());
                    queriedDownloadRealm.setDownloadDate(downloadRealm.getDownloadDate());
                    queriedDownloadRealm.setDownloadState(downloadRealm.getDownloadState());
                });
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
