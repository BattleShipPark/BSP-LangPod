package com.battleshippark.bsp_langpod.data.db;

import android.support.annotation.VisibleForTesting;

import com.annimon.stream.Stream;

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
    private ChannelDbRepository channelDbRepository;

    @VisibleForTesting
    DownloadDbApi() {
        this.realmConfiguration = RealmConfigurationFactory.createTest();
    }

    @Inject
    public DownloadDbApi(RealmConfiguration realmConfiguration, ChannelDbRepository channelDbRepository) {
        this.realmConfiguration = realmConfiguration;
        this.channelDbRepository = channelDbRepository;
    }

    @Override
    public Observable<List<DownloadRealm>> all() {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getInstance(realmConfiguration)) {
                RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class);
                RealmResults<DownloadRealm> results = query.sort(DownloadRealm.FIELD_DOWNLOAD_DATE).findAll();

                List<DownloadRealm> downloadRealmList = Stream.of(realm.copyFromRealm(results)).map(this::populate).toList();
                subscriber.onNext(downloadRealmList);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    private DownloadRealm populate(DownloadRealm downloadRealm) {
        ChannelRealm channelRealm = channelDbRepository.channel(downloadRealm.getChannelId()).toBlocking().first();

        EpisodeRealm episodeRealm = Stream.of(channelRealm.getEpisodes())
                .filter(episodeRealm1 -> episodeRealm1.getId() == downloadRealm.getEpisodeId())
                .findFirst().get();

        DownloadRealm result = new DownloadRealm(channelRealm, episodeRealm);
        result.setDownloadDate(downloadRealm.getDownloadDate());
        result.setDownloadState(downloadRealm.getDownloadState());
        return result;
    }

    @Override
    public Observable<List<DownloadRealm>> getNotDownloaded() {
        return Observable.create(subscriber -> {
            try (Realm realm = Realm.getInstance(realmConfiguration)) {
                RealmQuery<DownloadRealm> query = realm.where(DownloadRealm.class)
                        .equalTo(DownloadRealm.FIELD_DOWNLOAD_STATE, DownloadRealm.DownloadState.NOT_DOWNLOADED.name());
                RealmResults<DownloadRealm> results = query.sort(DownloadRealm.FIELD_DOWNLOAD_DATE).findAll();
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
                RealmResults<DownloadRealm> results = query.sort(DownloadRealm.FIELD_DOWNLOAD_DATE).findAll();
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
                    DownloadRealm queriedDownloadRealm = realm.where(DownloadRealm.class)
                            .equalTo(DownloadRealm.FIELD_ID, downloadRealm.getId()).findFirst();

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
