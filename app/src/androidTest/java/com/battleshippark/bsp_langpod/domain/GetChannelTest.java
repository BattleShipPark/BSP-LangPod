package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.LooperThread;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class GetChannelTest {
    @Test
    public void execute_전체리스트에서_조회() throws InterruptedException {
        List<ChannelRealm> channelRealmList = Arrays.asList( //DB에 있는 url1을 대상으로,
                new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", "cr1",
                        new RealmList<>(
                                new EpisodeRealm(1, "ep.title1", "ep.desc1", "ep.url1", 11, new Date()),
                                new EpisodeRealm(2, "ep.title2", "ep.desc2", "ep.url2", 22, new Date())
                        ), false
                ),
                new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", "cr2",
                        new RealmList<>(
                                new EpisodeRealm(3, "ep2.title1", "ep2.desc1", "ep2.url1", 33, new Date()),
                                new EpisodeRealm(4, "ep2.title2", "ep2.desc2", "ep2.url2", 44, new Date())
                        ), true
                )
        );
        ChannelJson channelJson = ChannelJson.create( //새로운 에피소드가 추가됐다
                "title1", "desc1", "cr1", "image1",
                Arrays.asList(
                        EpisodeJson.create("ep.title1", "ep.desc1", "ep.url1", 1, new Date()),
                        EpisodeJson.create("ep.title2", "ep.desc2", "ep.url2", 2, new Date()),
                        EpisodeJson.create("ep.title3", "ep.desc3", "ep.url3", 3, new Date())
                )
        );
        LooperThread thread = new LooperThread("GetChannelTest");
        Executor executor = thread.getExecutor();
        TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();
        thread.run(() -> {
            Realm realm = Realm.getDefaultInstance();
            ChannelServerRepository serverRepository = mock(ChannelServerRepository.class);
            when(serverRepository.myChannel("url1")).thenReturn(Observable.just(channelJson));
            UseCase<Long, List<ChannelRealm>> useCase = new GetChannel(new ChannelDbApi(), serverRepository,
                    Schedulers.io(), Schedulers.from(executor), new DomainMapper(mock(RealmHelper.class)));

            realm.executeTransaction(realm1 -> {
                realm1.delete(ChannelRealm.class);
                realm1.copyToRealm(channelRealmList);
            });



            useCase.execute(1L).subscribe(testSubscriber);
        });

        Thread.sleep(1000); //DB에 쓰는 비동기 작업이 끝날 시간을 준다

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        List<ChannelRealm> actualChannelRealmList = new ArrayList<>();
        thread.run(() -> {
            assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

            Realm realm = Realm.getDefaultInstance();
            actualChannelRealmList.add(realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0).get(0)));
        });
        thread.await();

        assertThat(actualChannelRealmList.get(0).getTitle()).isEqualTo("title1");
        assertThat(actualChannelRealmList.get(0).getEpisodes()).hasSize(3);
        assertThat(actualChannelRealmList.get(0).getEpisodes().get(2).getTitle()).isEqualTo("ep.title3");
    }
}