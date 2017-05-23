package com.battleshippark.bsp_langpod.domain;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EpisodeJson;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 */
public class GetChannelTest {
    @Mock
    ChannelServerRepository serverRepository;

    @Test
    public void execute_전체리스트에서_조회() throws InterruptedException {
        List<ChannelRealm> channelRealmList = Arrays.asList(
                new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", "cr1",
                        new RealmList<>(
                                new EpisodeRealm("ep.title1", "ep.desc1", "ep.url1"),
                                new EpisodeRealm("ep.title2", "ep.desc2", "ep.url2")
                        ), false
                ),
                new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", "cr2",
                        new RealmList<>(
                                new EpisodeRealm("ep2.title1", "ep2.desc1", "ep2.url1"),
                                new EpisodeRealm("ep2.title2", "ep2.desc2", "ep2.url2")
                        ), true
                )
        );
        ChannelJson channelJson = ChannelJson.create(
                "title1", "desc1", "cr1", "image1",
                Arrays.asList(
                        EpisodeJson.create("ep.title1", "ep.desc1", "ep.url1", 1, new Date()),
                        EpisodeJson.create("ep.title2", "ep.desc2", "ep.url2", 2, new Date()),
                        EpisodeJson.create("ep.title3", "ep.desc3", "ep.url3", 3, new Date())
                )
        );
        HandlerThread handlerThread = new HandlerThread("GetEntireChannelListTest");
        handlerThread.start();
        //Realm의 live update를 사용하려면 같은 쓰레드에서 만든 Realm 인스턴스를 사용해야 한다.
        //거기다 그 쓰레드가 루퍼를 가지고 있어야 하는데, 테스트를 돌리는 쓰레드는 루퍼를 가지고 있지 않다
        //모양은 예쁘지 않지만, 이렇게 직접 쓰레드와 루퍼를 만들어서 해결한다
        Handler handler = new Handler(handlerThread.getLooper());
        TestExecutor executor = new TestExecutor(handler);
        TestSubscriber<ChannelRealm> testSubscriber = new TestSubscriber<>();
        handler.post(() -> {
            Realm realm = Realm.getDefaultInstance();
            when(serverRepository.myChannel("url1")).thenReturn(Observable.just(channelJson));
            UseCase<Long, ChannelRealm> useCase = new GetChannel(new ChannelDbApi(realm), serverRepository,
                    Schedulers.io(), Schedulers.from(executor), new DomainMapper());


            useCase.execute(1L).subscribe(testSubscriber);
        });

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
        CountDownLatch latch = new CountDownLatch(1);
        List<ChannelRealm> actualChannelRealmList = new ArrayList<>();
        handler.post(() -> {
            Realm realm = Realm.getDefaultInstance();
            actualChannelRealmList.add(realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0)));
            latch.countDown();
        });
        latch.await();

        assertThat(actualChannelRealmList.get(0).getTitle()).isEqualTo("title1");
        assertThat(actualChannelRealmList.get(0).getEpisodes()).hasSize(3);
        assertThat(actualChannelRealmList.get(0).getEpisodes().get(2).getTitle()).isEqualTo("ep.title3");
    }

    private class TestExecutor implements Executor {
        private final Handler handler;

        TestExecutor(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    }
}