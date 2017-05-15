package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelData;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;
import com.battleshippark.bsp_langpod.data.server.MyChannelData;

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
        List<EntireChannelRealm> entireChannelRealmList = Arrays.asList(
                new EntireChannelRealm(1, 10, "title1", "desc1", "image1"),
                new EntireChannelRealm(2, 11, "title2", "desc2", "image2")
        );
        EntireChannelListData entireChannelListData = EntireChannelListData.create(
                Arrays.asList(
                        EntireChannelData.create(1, 10, "title2", "desc2", "image2"),
                        EntireChannelData.create(2, 11, "title3", "desc3", "image3")
                )
        );
        when(dbRepository.entireChannelList()).thenReturn(Observable.just(entireChannelRealmList));
        when(serverRepository.entireChannelList()).thenReturn(Observable.just(entireChannelListData));
        RealmMapper mapper = new RealmMapper();
        UseCase<Void, EntireChannelListData> useCase = new GetEntireChannelList(dbRepository, serverRepository, null, mapper);
        TestSubscriber<EntireChannelListData> testSubscriber = new TestSubscriber<>();
        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(2);

        EntireChannelListData dbEntireChannelListData = testSubscriber.getOnNextEvents().get(0);
        assertThat(dbEntireChannelListData.items()).hasSize(2);
        assertThat(dbEntireChannelListData.items().get(0).title()).isEqualTo("title1");
        assertThat(dbEntireChannelListData.items().get(1).desc()).isEqualTo("desc2");

        EntireChannelListData serverEntireChannelListData = testSubscriber.getOnNextEvents().get(1);
        assertThat(serverEntireChannelListData.items()).hasSize(2);
        assertThat(serverEntireChannelListData.items().get(0).title()).isEqualTo("title2");
        assertThat(serverEntireChannelListData.items().get(1).desc()).isEqualTo("desc3");

        verify(dbRepository).putEntireChannelList(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue().get(0).getTitle()).isEqualTo("title2");
        assertThat(captor.getValue().get(1).getDesc()).isEqualTo("desc3");
    }
}