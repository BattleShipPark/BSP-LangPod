package com.battleshippark.bsp_langpod.data.storedvalue;

import rx.Completable;
import rx.Observable;

/**
 */

public interface StoredValueRepository {
    enum KEY {
        DOWNLOAD_WIFI_ONLY,

    }

    Observable<Boolean> getDownloadWifiOnly();

    Completable putDownloadWifiOnly(boolean value);
}
