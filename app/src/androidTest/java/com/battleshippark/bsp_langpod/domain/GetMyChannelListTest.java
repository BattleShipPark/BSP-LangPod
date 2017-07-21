package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class GetMyChannelListTest {
    @Test
    public void execute() throws InterruptedException {
        Realm realm = Realm.getDefaultInstance();
        ChannelDbRepository dbRepository = new ChannelDbApi();
        List<ChannelRealm> channelRealmList = Arrays.asList(
                new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", false),
                new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", true)
        );
        dbRepository.putEntireChannelList(channelRealmList);
        UseCase<Void, List<ChannelRealm>> useCase = new GetMyChannelList(dbRepository);
        TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();



        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
        List<ChannelRealm> actualChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));

        //isSubscribed=true인 title2만 나와야 한다
        assertThat(actualChannelRealmList).hasSize(1);
        assertThat(actualChannelRealmList).containsExactlyElementsOf(
                Collections.singletonList(
                        new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", true)
                )
        );
    }
}