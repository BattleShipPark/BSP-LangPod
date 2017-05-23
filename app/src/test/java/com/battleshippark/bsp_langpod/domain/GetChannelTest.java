package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;

import io.realm.RealmList;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class GetChannelTest {
    @Mock
    ChannelDbRepository dbRepository;
    @Mock
    ChannelServerRepository serverRepository;
    @Captor
    ArgumentCaptor<ChannelRealm> captor;

    @Test
    public void execute_전체리스트에서_하나_조회() {
        ChannelRealm channelRealm = new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", "cr1",
                new RealmList<>(
                        new EpisodeRealm("ep.title1", "ep.desc1", "ep.url1"),
                        new EpisodeRealm("ep.title2", "ep.desc2", "ep.url2")
                ), false
        );
        ChannelJson channelJson = ChannelJson.create(
                "title1", "desc1", "cr1", "image1",
                Arrays.asList(
                        EpisodeJson.create("ep.title1", "ep.desc1", "ep.url1", 1, new Date()),
                        EpisodeJson.create("ep.title2", "ep.desc2", "ep.url2", 2, new Date()),
                        EpisodeJson.create("ep.title3", "ep.desc3", "ep.url3", 3, new Date())
                )
        );
        when(dbRepository.channel(1)).thenReturn(Observable.just(channelRealm));
        when(serverRepository.myChannel("url1")).thenReturn(Observable.just(channelJson));

        DomainMapper domainMapper = new DomainMapper();
        UseCase<Long, ChannelRealm> useCase = new GetChannel(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);
        TestSubscriber<ChannelRealm> testSubscriber = new TestSubscriber<>();


        useCase.execute(1L).subscribe(testSubscriber); //1번ID 조회


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        ChannelRealm actualMyChannelRealm = testSubscriber.getOnNextEvents().get(0);
        assertThat(actualMyChannelRealm).isEqualTo(channelRealm);

        verify(dbRepository).putChannel(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("title1");
        assertThat(captor.getValue().getEpisodes()).hasSize(3);
        assertThat(captor.getValue().getEpisodes().get(2).getTitle()).isEqualTo("ep.title3");
    }
}