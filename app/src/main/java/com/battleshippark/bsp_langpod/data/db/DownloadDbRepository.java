package com.battleshippark.bsp_langpod.data.db;

import java.util.List;

import rx.Completable;
import rx.Observable;

/**
 */

public interface DownloadDbRepository {
    Observable<List<DownloadRealm>> all();

    Observable<List<DownloadRealm>> getNotDownloaded();

    Completable add(EpisodeRealm episodeRealm);

    Completable remove(EpisodeRealm episodeRealm);
}
