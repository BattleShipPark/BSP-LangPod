package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelData;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;
import com.battleshippark.bsp_langpod.data.server.MyChannelData;

import org.junit.Test;

import java.util.Arrays;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class GetEntireChannelListTest {
    @Test
    public void execute() {
        EntireChannelListData entireChannelListData = EntireChannelListData.create(
                Arrays.asList(
                        EntireChannelData.create("title1", "desc1", "image1"),
                        EntireChannelData.create("title2", "desc2", "image2")
                )
        );
        ChannelServerRepository apiRepository = new ChannelServerRepository() {
            @Override
            public Observable<EntireChannelListData> entireChannelList() {
                return Observable.just(entireChannelListData);
            }

            @Override
            public Observable<MyChannelData> channel(String url) {
                return null;
            }
        };
        TestSubscriber<EntireChannelListData> testSubscriber = new TestSubscriber<>();
        UseCase<Void, EntireChannelListData> useCase = new GetEntireChannelList(apiRepository, null);


        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(entireChannelListData);
    }
}