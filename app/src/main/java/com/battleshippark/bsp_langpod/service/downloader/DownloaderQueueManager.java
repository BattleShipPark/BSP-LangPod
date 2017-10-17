package com.battleshippark.bsp_langpod.service.downloader;

import com.battleshippark.bsp_langpod.data.db.DownloadDbRepository;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;

import java.util.List;

/**
 */

public class DownloaderQueueManager {
    private static DownloaderQueueManager MANAGER;
    private final DownloaderQueue queue = new DownloaderQueue();
    private final DownloadDbRepository downloadDbApi;

    public static void create(DownloadDbRepository repository) {
        MANAGER = new DownloaderQueueManager(repository);
    }

    static DownloaderQueueManager getInstance() {
        if (MANAGER == null) {
            throw new RuntimeException();
        }
        return MANAGER;
    }

    DownloaderQueueManager(DownloadDbRepository repository) {
        downloadDbApi = repository;
    }

    void offer(DownloadRealm downloadRealm) {
        downloadDbApi.insert(downloadRealm);
        queue.offer(downloadRealm);
    }

    DownloadRealm peek() throws InterruptedException {
        return queue.peek();
    }

    void clearWith(List<DownloadRealm> downloadRealms) {
        queue.clearWith(downloadRealms);
    }

    void markComplete(DownloadRealm downloadRealm) {
        downloadRealm.setDownloadState(DownloadRealm.DownloadState.DOWNLOADED);
        downloadDbApi.update(downloadRealm);
    }

    void markError(DownloadRealm downloadRealm) {
        downloadRealm.setDownloadState(DownloadRealm.DownloadState.FAILED_DOWNLOAD);
        downloadDbApi.update(downloadRealm);
    }

    void markDownloading(DownloadRealm downloadRealm) {
        downloadRealm.setDownloadState(DownloadRealm.DownloadState.DOWNLOADING);
        downloadDbApi.update(downloadRealm);
    }
}
