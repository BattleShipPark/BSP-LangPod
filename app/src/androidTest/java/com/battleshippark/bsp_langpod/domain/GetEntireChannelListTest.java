package com.battleshippark.bsp_langpod.domain;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.BuildConfig;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.server.ChannelServerApi;
import com.battleshippark.bsp_langpod.data.server.EntireChannelJson;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;
import com.battleshippark.bsp_langpod.data.server.rss.RssResponseMapper;

import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    public void execute() {
        HandlerThread handlerThread = new HandlerThread("GetEntireChannelListTest");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        TestExecutor executor = new TestExecutor(handler);
        TestSubscriber<List<EntireChannelData>> testSubscriber = new TestSubscriber<>();
        handler.post(() -> {
            Realm realm = Realm.getDefaultInstance();
            ChannelDbRepository dbRepository = new ChannelDbApi(realm);

            List<EntireChannelRealm> entireChannelRealmList = Arrays.asList(
                    new EntireChannelRealm(1, 10, "title1", "desc1", "image1"),
                    new EntireChannelRealm(2, 11, "title2", "desc2", "image2")
            );
            dbRepository.putEntireChannelList(entireChannelRealmList); //DB를 읽어 놓고

            ChannelServerApi serverRepository = mock(ChannelServerApi.class);
            when(serverRepository.entireChannelList()).thenReturn(
                    Observable.just(
                            EntireChannelListJson.create(
                                    Collections.singletonList(EntireChannelJson.create(3, 12, "title3", "desc3", "image3"))
                                    //DB와 다른 값이 서버에서 내려오면
                            )
                    )
            );
            Scheduler scheduler = Schedulers.io();
            DomainMapper domainMapper = new DomainMapper();
            UseCase<Void, List<EntireChannelData>> useCase = new GetEntireChannelList(dbRepository, serverRepository,
                    scheduler, Schedulers.from(executor), domainMapper);

            Log.w("thread", Thread.currentThread().getName());
            useCase.execute(null).subscribe(testSubscriber);
        });

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
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