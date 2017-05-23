package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.server.EntireChannelJson;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
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
import rx.schedulers.Schedulers;

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
    ArgumentCaptor<List<ChannelRealm>> captor;

    @Test
    public void execute() {
        //1번 ID가 삭제되고 3번 ID가 추가되었다
        List<ChannelRealm> channelRealmList = Arrays.asList(
                new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", false),
                new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", true)
        );
        EntireChannelListJson entireChannelListJson = EntireChannelListJson.create(
                Arrays.asList(
                        EntireChannelJson.create(2, 10, "title2", "desc2", "image2", "url2"),
                        EntireChannelJson.create(3, 11, "title3", "desc3", "image3", "url3")
                )
        );
        when(dbRepository.entireChannelList()).thenReturn(Observable.just(channelRealmList));
        when(serverRepository.entireChannelList()).thenReturn(Observable.just(entireChannelListJson));

        DomainMapper domainMapper = new DomainMapper();
        UseCase<Void, List<ChannelRealm>> useCase = new GetEntireChannelList(dbRepository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);
        TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();


        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        //db에서 제대로 읽었는지
        List<ChannelRealm> dbChannelRealmList = testSubscriber.getOnNextEvents().get(0);
        assertThat(dbChannelRealmList).hasSize(2);
        assertThat(dbChannelRealmList).containsExactlyElementsOf(channelRealmList);

        //서버에서 내려온 값을 제대로 병합했는지
        verify(dbRepository).putEntireChannelList(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).getId()).isEqualTo(2);
        assertThat(captor.getValue().get(0).getTitle()).isEqualTo("title2");
        assertThat(captor.getValue().get(0).isSubscribed()).isEqualTo(true);
        assertThat(captor.getValue().get(1).getId()).isEqualTo(3);
        assertThat(captor.getValue().get(1).getDesc()).isEqualTo("desc3");
        assertThat(captor.getValue().get(1).isSubscribed()).isEqualTo(false);
    }
}