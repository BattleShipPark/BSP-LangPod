package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelJson;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class GetEntireChannelListTest {
    @Mock
    ChannelDbRepository dbRepository;
    @Mock
    ChannelServerRepository serverRepository;
    @Captor
    ArgumentCaptor<List<EntireChannelRealm>> captor;

    @Test
    public void execute() {
        //1번 ID가 삭제되고 3번 ID가 추가되었다
        List<EntireChannelRealm> entireChannelRealmList = Arrays.asList(
                new EntireChannelRealm(1, 10, "title1", "desc1", "image1"),
                new EntireChannelRealm(2, 11, "title2", "desc2", "image2")
        );
        EntireChannelListJson entireChannelListJson = EntireChannelListJson.create(
                Arrays.asList(
                        EntireChannelJson.create(2, 10, "title2", "desc2", "image2"),
                        EntireChannelJson.create(3, 11, "title3", "desc3", "image3")
                )
        );
        when(dbRepository.entireChannelList()).thenReturn(Observable.just(entireChannelRealmList));
        when(serverRepository.entireChannelList()).thenReturn(Observable.just(entireChannelListJson));

        DomainMapper domainMapper = new DomainMapper();
        UseCase<Void, List<EntireChannelRealm>> useCase = new GetEntireChannelList(dbRepository, serverRepository, null, null, domainMapper);
        TestSubscriber<List<EntireChannelRealm>> testSubscriber = new TestSubscriber<>();



        useCase.execute(null).subscribe(testSubscriber);



        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        List<EntireChannelRealm> dbEntireChannelDataList = testSubscriber.getOnNextEvents().get(0);
        assertThat(dbEntireChannelDataList).hasSize(2);
        assertThat(dbEntireChannelDataList.get(0).getId()).isEqualTo(1);
        assertThat(dbEntireChannelDataList.get(0).getTitle()).isEqualTo("title1");
        assertThat(dbEntireChannelDataList.get(1).getId()).isEqualTo(2);
        assertThat(dbEntireChannelDataList.get(1).getDesc()).isEqualTo("desc2");

        verify(dbRepository).putEntireChannelList(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).getId()).isEqualTo(2);
        assertThat(captor.getValue().get(0).getTitle()).isEqualTo("title2");
        assertThat(captor.getValue().get(1).getId()).isEqualTo(3);
        assertThat(captor.getValue().get(1).getDesc()).isEqualTo("desc3");
    }
}