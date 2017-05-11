package com.battleshippark.bsp_langpod.domain;

import rx.Observable;

/**
 */

public interface UseCase<P, R> {
    Observable<R> execute(P param);
}
