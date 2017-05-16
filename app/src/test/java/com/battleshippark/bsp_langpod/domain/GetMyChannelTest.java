package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import io.realm.RealmList;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class GetMyChannelTest {
    @Mock
    ChannelDbRepository dbRepository;

    @Test
    public void execute() {
        MyChannelRealm myChannelRealm = new MyChannelRealm(1, 10, "title1", "desc1", "cr1", "image1",
                new RealmList<>(new EpisodeRealm("ep.title1", "ep.desc1", "ep.url1")));
        when(dbRepository.myChannel(0)).thenReturn(Observable.just(myChannelRealm));
        RealmMapper mapper = new RealmMapper();
        UseCase<Void, MyChannelData> useCase = new GetMyChannel(dbRepository, null, null, mapper);
        TestSubscriber<MyChannelData> testSubscriber = new TestSubscriber<>();
        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        MyChannelData actualMyChannelData = testSubscriber.getOnNextEvents().get(0);
        assertThat(actualMyChannelData)
                .isEqualTo(MyChannelData.create(1, 10, "title1", "desc1", "cr1", "image1",
                        Collections.singletonList(EpisodeData.create("ep.title1", "ep.desc1", "ep.url1"))));
    }
}