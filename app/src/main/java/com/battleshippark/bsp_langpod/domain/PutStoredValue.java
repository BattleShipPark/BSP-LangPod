package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.storedvalue.StoredValueRepository;

import rx.Completable;

/**
 */

public class PutStoredValue implements PutStoredValueUseCase {
    private StoredValueRepository repository;

    public PutStoredValue(StoredValueRepository repository) {
        this.repository = repository;
    }
    @Override
    public Completable downloadOnlyWifi(boolean value) {
        return repository.putDownloadWifiOnly(value);
    }
}
