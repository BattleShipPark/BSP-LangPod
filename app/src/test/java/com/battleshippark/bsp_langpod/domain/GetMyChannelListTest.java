package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

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
        List<ChannelRealm> myChannelRealmList = Collections.singletonList(
                new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", true)
        );
        when(dbRepository.myChannelList()).thenReturn(Observable.just(myChannelRealmList));
        UseCase<Void, List<ChannelRealm>> useCase = new GetMyChannelList(dbRepository, Schedulers.immediate(), Schedulers.immediate());
        TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();


        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        List<ChannelRealm> actualMyChannelRealmList = testSubscriber.getOnNextEvents().get(0);
        assertThat(actualMyChannelRealmList).hasSize(1);
        assertThat(actualMyChannelRealmList.get(0))
                .isEqualTo(new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", true));
    }
}