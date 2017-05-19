package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;
import com.battleshippark.bsp_langpod.data.server.MyChannelJson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import io.realm.RealmList;
import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class GetMyChannelTest {
    @Mock
    ChannelDbRepository dbRepository;
    @Mock
    ChannelServerRepository serverRepository;
    @Captor
    ArgumentCaptor<MyChannelRealm> captor;

    @Test
    public void execute() {
        MyChannelRealm myChannelRealm = new MyChannelRealm(1, 10, "title1", "desc1", "cr1", "image1", "url1",
                new RealmList<>(new EpisodeRealm("ep.title1", "ep.desc1", "ep.url1")));
        MyChannelJson myChannelJson = MyChannelJson.create(
                "title1", "desc1", "cr1", "image1",
                Arrays.asList(
                        EpisodeJson.create("ep.title1", "ep.desc1", "ep.url1"),
                        EpisodeJson.create("ep.title2", "ep.desc2", "ep.url2")
                )
        );
        when(dbRepository.myChannel(1)).thenReturn(Observable.just(myChannelRealm));
        when(serverRepository.myChannel(any())).thenReturn(Observable.just(myChannelJson));

        DomainMapper domainMapper = new DomainMapper();
        UseCase<MyChannelData, MyChannelRealm> useCase = new GetMyChannel(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);
        TestSubscriber<MyChannelRealm> testSubscriber = new TestSubscriber<>();


        useCase.execute(MyChannelData.create(1, 10, "title1", "desc1", "cr1", "image1", "url1", null))
                .subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        MyChannelRealm actualMyChannelRealm = testSubscriber.getOnNextEvents().get(0);
        assertThat(actualMyChannelRealm).isEqualTo(myChannelRealm);
        verify(dbRepository).putMyChannel(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("title1");
        assertThat(captor.getValue().getItems()).hasSize(2);
        assertThat(captor.getValue().getItems().get(0).getTitle()).isEqualTo("ep.title1");
        assertThat(captor.getValue().getItems().get(1).getTitle()).isEqualTo("ep.title2");
    }
}