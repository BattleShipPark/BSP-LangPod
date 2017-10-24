package com.battleshippark.bsp_langpod.service.downloader;

import com.battleshippark.bsp_langpod.data.db.DownloadRealm;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 */

class DownloaderQueue {
    private final BlockingDeque<DownloadRealm> queue = new LinkedBlockingDeque<>();

    boolean offer(DownloadRealm downloadRealm) {
        return queue.offer(downloadRealm);
    }

    DownloadRealm take() throws InterruptedException {
        return queue.take();
    }

    DownloadRealm peek() throws InterruptedException {
        DownloadRealm e = queue.take();
        queue.offerFirst(e);
        return e;
    }

    void remove(DownloadRealm downloadRealm) {
        queue.remove(downloadRealm);
    }

    void clearWith(List<DownloadRealm> list) {
        queue.clear();
        queue.addAll(list);
    }
}
