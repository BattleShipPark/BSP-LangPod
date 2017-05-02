package com.battleshippark.bsp_langpod.data;

import rx.Observable;

/**
 */

public interface ChannelRepository<T> {
    Observable<T> query(String url);
}
