package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.storedvalue.StoredValueRepository;

import rx.Observable;

/**
 */

public class GetStoredValue implements GetStoredValueUseCase {
    private StoredValueRepository repository;

    public GetStoredValue(StoredValueRepository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<Boolean> downloadOnlyWifi() {
        return repository.getDownloadWifiOnly();
    }
}
