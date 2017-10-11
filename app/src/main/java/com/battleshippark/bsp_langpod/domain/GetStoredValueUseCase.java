package com.battleshippark.bsp_langpod.domain;


import rx.Observable;

/**
 */

interface GetStoredValueUseCase {
    Observable<Boolean> downloadOnlyWifi();
}
