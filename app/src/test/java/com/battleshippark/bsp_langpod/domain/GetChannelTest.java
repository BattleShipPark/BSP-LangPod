package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;

import io.realm.RealmList;
import rx.Completable;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
    @Mock
    RealmHelper realmHelper;
    @Captor
    ArgumentCaptor<ChannelRealm> captor;

    DomainMapper domainMapper;

    ChannelRealm channelRealm = new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", "cr1",
            new RealmList<>(
                    new EpisodeRealm(1, "ep.title1", "ep.desc1", "ep.url1", new Date()),
                    new EpisodeRealm(2, "ep.title2", "ep.desc2", "ep.url2", new Date())
            ), false
    );
    ChannelJson channelJson = ChannelJson.create(
            "title1", "desc1", "cr1", "image1",
            Arrays.asList(
                    EpisodeJson.create("ep.title1", "ep.desc1", "ep.url1", new Date()),
                    EpisodeJson.create("ep.title2", "ep.desc2", "ep.url2", new Date()),
                    EpisodeJson.create("ep.title3", "ep.desc3", "ep.url3", new Date())
            )
    );
    TestSubscriber<ChannelRealm> testSubscriber = new TestSubscriber<>();

    @Before
    public void setup() {
        domainMapper = new DomainMapper(realmHelper);
    }

    @Test
    public void execute_호출상태_DB() {
        when(dbRepository.channel(1)).thenReturn(Observable.just(channelRealm));

        GetChannel getChannel = new GetChannel(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);
        getChannel.execute(new GetChannel.Param(1, GetChannel.Type.ONLY_DB)).subscribe(testSubscriber);

        verify(serverRepository, never()).myChannel(any());

        testSubscriber.assertCompleted();
        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
        assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(channelRealm);
    }

    @Test
    public void execute_호출상태_DB_예외발생() {
        when(dbRepository.channel(1)).thenReturn(Observable.error(new Exception()));

        GetChannel getChannel = new GetChannel(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);
        getChannel.execute(new GetChannel.Param(1, GetChannel.Type.ONLY_DB)).subscribe(testSubscriber);

        testSubscriber.assertNotCompleted();
        assertThat(testSubscriber.getOnErrorEvents()).hasSize(1);
        GetChannel.GetChannelThrowable throwable = (GetChannel.GetChannelThrowable) testSubscriber.getOnErrorEvents().get(0);
        assertThat(throwable.getType()).isEqualTo(GetChannel.Type.ONLY_DB);
    }

    @Test
    public void execute_호출상태_DB_NETWORK() {
        when(dbRepository.channel(1)).thenReturn(Observable.just(channelRealm));
        when(serverRepository.myChannel("url1")).thenReturn(Observable.just(channelJson));

        GetChannel getChannel = new GetChannel(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);
        getChannel.execute(new GetChannel.Param(1, GetChannel.Type.DB_AND_SERVER)).subscribe(testSubscriber);

        verify(serverRepository).myChannel(any());
        verify(dbRepository).putChannel(any());
    }

    @Test
    public void execute_호출상태_DB_NETWORK_예외발생() {
        when(dbRepository.channel(1)).thenReturn(Observable.just(channelRealm));
        when(serverRepository.myChannel("url1")).thenReturn(Observable.error(new Exception()));

        GetChannel getChannel = new GetChannel(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);
        getChannel.execute(new GetChannel.Param(1, GetChannel.Type.DB_AND_SERVER)).subscribe(testSubscriber);

        testSubscriber.assertNotCompleted();
        assertThat(testSubscriber.getOnErrorEvents()).hasSize(1);
        GetChannel.GetChannelThrowable throwable = (GetChannel.GetChannelThrowable) testSubscriber.getOnErrorEvents().get(0);
        assertThat(throwable.getType()).isEqualTo(GetChannel.Type.DB_AND_SERVER);
    }

    @Test
    public void execute_전체리스트에서_한건_조회하는데_에피소드가_추가되어있다() {
        when(dbRepository.channel(1)).thenReturn(Observable.just(channelRealm));
        when(dbRepository.putChannel(any())).thenReturn(Completable.complete());
        when(serverRepository.myChannel("url1")).thenReturn(Observable.just(channelJson));

        GetChannel getChannel = new GetChannel(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);


        getChannel.execute(new GetChannel.Param(1L, GetChannel.Type.DB_AND_SERVER)).subscribe(testSubscriber); //1번ID 조회


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(2);
        assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(channelRealm);

        ChannelRealm actualChannelRealm = testSubscriber.getOnNextEvents().get(1);
        assertThat(actualChannelRealm.getTitle()).isEqualTo("title1");
        assertThat(actualChannelRealm.getEpisodes()).hasSize(3);
        assertThat(actualChannelRealm.getEpisodes().get(2).getTitle()).isEqualTo("ep.title3");

        verify(dbRepository).putChannel(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("title1");
        assertThat(captor.getValue().getEpisodes()).hasSize(3);
        assertThat(captor.getValue().getEpisodes().get(2).getTitle()).isEqualTo("ep.title3");
    }
}