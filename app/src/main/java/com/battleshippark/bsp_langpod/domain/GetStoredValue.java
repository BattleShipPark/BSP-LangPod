package com.battleshippark.bsp_langpod.domain;

import rx.Observable;

/**
 */

public class GetStoredValue implements GetStoredValueUseCase {
    @Override
    public Observable<Boolean> downloadOnlyWifi() {
        return Observable.just(true);
    }
}
