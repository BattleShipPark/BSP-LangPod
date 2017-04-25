package com.battleshippark.bsp_langpod.data;

import rx.Observable;

/**
 */

public interface ChannelInteractor<T> {
    Observable<T> query(String url);
}
