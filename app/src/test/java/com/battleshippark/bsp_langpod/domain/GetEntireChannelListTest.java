package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelData;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListData;
import com.battleshippark.bsp_langpod.data.server.MyChannelData;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class GetEntireChannelListTest {
    @Test
    public void execute() {
        List<EntireChannelRealm> entireChannelRealmList = Arrays.asList(
                new EntireChannelRealm(1, "title1", "desc1", "image1"),
                new EntireChannelRealm(2, "title2", "desc2", "image2")
        );
        EntireChannelListData entireChannelListData = EntireChannelListData.create(
                Arrays.asList(
                        EntireChannelData.create("title2", "desc2", "image2"),
                        EntireChannelData.create("title3", "desc3", "image3")
                )
        );
        ChannelDbRepository dbRepository = new ChannelDbRepository() {
            @Override
            public Observable<List<EntireChannelRealm>> entireChannelList() {
                return Observable.just(entireChannelRealmList);
            }

            @Override
            public Observable<EntireChannelListData> queryAll() {
                return null;
            }

            @Override
            public Observable<MyChannelData> query(int id) {
                return null;
            }
        };
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
        RealmMapper mapper = new RealmMapper();
        UseCase<Void, EntireChannelListData> useCase = new GetEntireChannelList(dbRepository, apiRepository, null, mapper);
        TestSubscriber<EntireChannelListData> testSubscriber = new TestSubscriber<>();


        useCase.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(2);
        EntireChannelListData dbEntireChannelListData = testSubscriber.getOnNextEvents().get(0);
        assertThat(dbEntireChannelListData.items()).hasSize(2);
        EntireChannelListData serverEntireChannelListData = testSubscriber.getOnNextEvents().get(1);
        assertThat(serverEntireChannelListData.items()).hasSize(2);
    }
}