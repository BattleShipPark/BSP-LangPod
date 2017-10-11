package com.battleshippark.bsp_langpod.data.storedvalue;

import android.content.Context;
import android.content.SharedPreferences;

import rx.Completable;
import rx.Observable;

/**
 */

public class StoredValueApi implements StoredValueRepository {
    private final SharedPreferences sp;

    public StoredValueApi(Context context) {
        sp = context.getSharedPreferences("StoredValue", Context.MODE_PRIVATE);
    }

    @Override
    public Observable<Boolean> getDownloadWifiOnly() {
        return Observable.just(sp.getBoolean(KEY.DOWNLOAD_WIFI_ONLY.name(), true));
    }

    @Override
    public Completable putDownloadWifiOnly(boolean value) {
        try {
            sp.edit().putBoolean(KEY.DOWNLOAD_WIFI_ONLY.name(), value).apply();
            return Completable.complete();
        } catch (Exception e) {
            return Completable.error(e);
        }
    }
}
