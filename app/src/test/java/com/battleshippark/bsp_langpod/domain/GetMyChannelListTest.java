package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class GetMyChannelListTest {
    @Mock
    ChannelDbRepository dbRepository;

    @Test
    public void execute() {
        List<MyChannelRealm> myChannelRealmList = Arrays.asList(
                new MyChannelRealm(1, 10, "title1", "desc1", "cr1", "image1", "url1", null),
                new MyChannelRealm(2, 11, "title2", "desc2", "cr2", "image2", "url2", null)
        );
        when(dbRepository.myChannelList()).thenReturn(Observable.just(myChannelRealmList));
        RealmMapper mapper = new RealmMapper();
        UseCase<Void, List<MyChannelData>> useCase = new GetMyChannelList(dbRepository, null, mapper);
        TestSubscriber<List<MyChannelData>> testSubscriber = new TestSubscriber<>();
        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        List<MyChannelData> actualMyChannelDataList = testSubscriber.getOnNextEvents().get(0);
        assertThat(actualMyChannelDataList).hasSize(2);
        assertThat(actualMyChannelDataList.get(0))
                .isEqualTo(MyChannelData.create(1, 10, "title1", "desc1", "cr1", "image1", "url1", null));
        assertThat(actualMyChannelDataList.get(1))
                .isEqualTo(MyChannelData.create(2, 11, "title2", "desc2", "cr2", "image2", "url2", null));
    }
}