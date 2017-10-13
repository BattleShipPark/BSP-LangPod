package com.battleshippark.bsp_langpod.service.downloader;

import com.battleshippark.bsp_langpod.data.db.DownloadDbRepository;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DownloaderQueueManagerTest {
    @Mock
    DownloadDbRepository repository;

    @Test
    public void offer_peek() throws InterruptedException {
        DownloaderQueueManager.create(repository);
        DownloaderQueueManager manager = DownloaderQueueManager.getInstance();
        DownloadRealm downloadRealm = new DownloadRealm();
        manager.offer(downloadRealm);

        assertThat(manager.peek()).isEqualTo(downloadRealm);

        assertThat(manager.peek()).isEqualTo(downloadRealm); //one more time
    }

    @Test
    public void peek한결과를수정() throws InterruptedException {
        DownloaderQueueManager.create(repository);
        DownloaderQueueManager manager = DownloaderQueueManager.getInstance();
        DownloadRealm downloadRealm = new DownloadRealm();
        manager.offer(downloadRealm);

        downloadRealm = manager.peek();
        manager.markComplete(downloadRealm);
        assertThat(manager.peek().getDownloadState()).isEqualTo(DownloadRealm.DownloadState.DOWNLOADED);

        downloadRealm = manager.peek();
        manager.markDownloading(downloadRealm);
        assertThat(manager.peek().getDownloadState()).isEqualTo(DownloadRealm.DownloadState.DOWNLOADING);

        downloadRealm = manager.peek();
        manager.markError(downloadRealm);
        assertThat(manager.peek().getDownloadState()).isEqualTo(DownloadRealm.DownloadState.FAILED_DOWNLOAD);
    }

}