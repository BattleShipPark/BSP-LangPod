package com.battleshippark.bsp_langpod.domain;

import rx.Completable;

/**
 */

interface PutStoredValueUseCase {
    Completable downloadOnlyWifi(boolean value);
}
