package com.battleshippark.bsp_langpod.service.downloader;

import com.battleshippark.bsp_langpod.dagger.DaggerDomainMapperGraph;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.DownloadDbRepository;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.domain.DomainMapper;

import org.junit.Before;
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

    DomainMapper domainMapper = DaggerDomainMapperGraph.create().domainMapper();
    DownloaderQueueManager manager;
    ChannelRealm channelRealm;
    EpisodeRealm episodeRealm;

    @Before
    public void setup() {
        DownloaderQueueManager.create(repository, domainMapper);
        manager = DownloaderQueueManager.getInstance();

        channelRealm = new ChannelRealm();
        channelRealm.setId(0x12);
        episodeRealm = new EpisodeRealm();
        episodeRealm.setId(0x23);
    }

    @Test
    public void offer_peek() throws InterruptedException {
        manager.offer(channelRealm, episodeRealm);

        DownloadRealm downloadRealm = manager.peek();
        assertThat(downloadRealm.getChannelRealm()).isEqualTo(channelRealm);
        assertThat(downloadRealm.getEpisodeRealm()).isEqualTo(episodeRealm);

        downloadRealm = manager.peek();
        assertThat(downloadRealm.getChannelRealm()).isEqualTo(channelRealm);
        assertThat(downloadRealm.getEpisodeRealm()).isEqualTo(episodeRealm); //one more time
    }

    @Test
    public void peek한결과를수정() throws InterruptedException {
        manager.offer(channelRealm, episodeRealm);

        DownloadRealm downloadRealm = manager.peek();
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