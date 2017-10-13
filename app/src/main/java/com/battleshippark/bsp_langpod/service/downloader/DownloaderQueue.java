package com.battleshippark.bsp_langpod.service.downloader;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 */

class DownloaderQueue<E> {
    private static final DownloaderQueue<?> QUEUE = new DownloaderQueue<>();
    private final BlockingQueue<E> queue = new LinkedBlockingQueue<>();

    static <T> DownloaderQueue<T> getInstance() {
        return (DownloaderQueue<T>) QUEUE;
    }

    private DownloaderQueue() {
    }

    boolean offer(E e) {
        return queue.offer(e);
    }

    E take() throws InterruptedException {
        return queue.take();
    }

    void clearWith(List<E> list) {
        queue.clear();
        queue.addAll(list);
    }
}
