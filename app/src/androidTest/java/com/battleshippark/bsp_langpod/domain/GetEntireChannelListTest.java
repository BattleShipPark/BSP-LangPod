package com.battleshippark.bsp_langpod.domain;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.ChannelJson;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import io.realm.Realm;
import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class GetEntireChannelListTest {
    @Test
    public void execute() throws InterruptedException {
        HandlerThread handlerThread = new HandlerThread("GetEntireChannelListTest");
        handlerThread.start();
        //Realm의 live update를 사용하려면 같은 쓰레드에서 만든 Realm 인스턴스를 사용해야 한다.
        //거기다 그 쓰레드가 루퍼를 가지고 있어야 하는데, 테스트를 돌리는 쓰레드는 루퍼를 가지고 있지 않다
        //모양은 예쁘지 않지만, 이렇게 직접 쓰레드와 루퍼를 만들어서 해결한다
        Handler handler = new Handler(handlerThread.getLooper());
        TestExecutor executor = new TestExecutor(handler);
        TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();
        handler.post(() -> {
            Realm realm = Realm.getDefaultInstance();
            ChannelDbRepository dbRepository = new ChannelDbApi(realm);

            List<ChannelRealm> channelRealmList = Arrays.asList(
                    new ChannelRealm(1, 10, "title1", "desc1", "image1"),
                    new ChannelRealm(2, 11, "title2", "desc2", "image2")
            );
            dbRepository.putEntireChannelList(channelRealmList); //DB를 읽어 놓고

            ChannelServerRepository serverRepository = mock(ChannelServerRepository.class);
            when(serverRepository.entireChannelList()).thenReturn(
                    Observable.just(
                            EntireChannelListJson.create(
                                    Collections.singletonList(ChannelJson.create(3, 12, "title3", "desc3", "image3"))
                                    //DB와 다른 값이 서버에서 내려오면
                            )
                    )
            );
            Scheduler scheduler = Schedulers.io();
            DomainMapper domainMapper = new DomainMapper();
            UseCase<Void, List<ChannelRealm>> useCase = new GetEntireChannelList(dbRepository, serverRepository,
                    scheduler, Schedulers.from(executor), domainMapper);

            useCase.execute(null).subscribe(testSubscriber);
        });

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
        CountDownLatch latch = new CountDownLatch(1);
        handler.post(() -> {
            Realm realm = Realm.getDefaultInstance();
            List<ChannelRealm> actualChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));
            assertThat(actualChannelRealmList).hasSize(1);
            assertThat(actualChannelRealmList.get(0)).isEqualTo(new ChannelRealm(3, 12, "title3", "desc3", "image3"));
            latch.countDown();
        });
        latch.await();
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