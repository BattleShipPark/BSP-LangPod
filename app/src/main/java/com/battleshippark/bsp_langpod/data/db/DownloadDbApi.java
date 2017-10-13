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
    public DownloadDbApi() {
        this.realmConfiguration = RealmConfigurationFactory.createTest();
    }

    @Inject
    public DownloadDbApi(RealmConfiguration realmConfiguration) {
        this.realmConfiguration = realmConfiguration;
    }

    @Override
    public Observable<List<DownloadRealm>> all() {
        try (Realm realm = Realm.getInstance(realmConfiguration)) {
            RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class);
            RealmResults<DownloadRealm> results = query.findAll();
            return Observable.just(realm.copyFromRealm(results));
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<List<DownloadRealm>> getNotDownloaded() {
        try (Realm realm = Realm.getInstance(realmConfiguration)) {
            RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class)
                    .equalTo(DownloadRealm.FIELD_DOWNLOAD_STATE, DownloadRealm.DownloadState.NOT_DOWNLOADED.name());
            RealmResults<DownloadRealm> results = query.findAllSorted(DownloadRealm.FIELD_DOWNLOAD_DATE);
            return Observable.just(realm.copyFromRealm(results));
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public Completable add(EpisodeRealm episodeRealm) {
        return null;
    }

    @Override
    public Completable remove(EpisodeRealm episodeRealm) {
        return null;
    }
}
