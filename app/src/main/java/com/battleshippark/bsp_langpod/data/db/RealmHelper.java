package com.battleshippark.bsp_langpod.data.db;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmModel;

/**
 */

public interface RealmHelper {
    long getNextEpisodeId();

    long getNextDownloadId();

    <T extends RealmModel> List<T> fromRealm(RealmList<T> episodes);
}
