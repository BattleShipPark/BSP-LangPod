package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.RealmConfigurationFactory;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;
import com.battleshippark.bsp_langpod.data.server.ChannelServerRepository;
import com.battleshippark.bsp_langpod.data.server.EntireChannelJson;
import com.battleshippark.bsp_langpod.data.server.EntireChannelListJson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class GetEntireChannelListTest {
    private Realm realm;
    private RealmConfiguration configuration;
    private ChannelDbRepository repository;

    @Before
    public void before() {
        configuration = RealmConfigurationFactory.createTest();
        Realm.deleteRealm(configuration);
        realm = Realm.getInstance(configuration);
        repository = new ChannelDbApi(configuration);
    }

    @After
    public void after() {
        realm.close();
    }

    @Test
    public void execute() throws InterruptedException {
        TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();

        List<ChannelRealm> channelRealmList = Arrays.asList(
                new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", false),
                new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", true)
        );
        repository.putEntireChannelList(channelRealmList).subscribe(); //DB에 넣어 놓고

        ChannelServerRepository serverRepository = mock(ChannelServerRepository.class);
        when(serverRepository.entireChannelList()).thenReturn(
                Observable.just(
                        EntireChannelListJson.create(
                                Arrays.asList(
                                        EntireChannelJson.create(2, 10, "title2", "desc2", "image2", "url2"),
                                        EntireChannelJson.create(3, 11, "title3", "desc3", "image3", "url3")
                                )
                                //DB와 다른 값이 서버에서 내려오면
                        )
                )
        );
        DomainMapper domainMapper = new DomainMapper(mock(RealmHelper.class));
        GetEntireChannelList getEntireChannelList = new GetEntireChannelList(repository, serverRepository,
                Schedulers.immediate(), Schedulers.immediate(), domainMapper);


        getEntireChannelList.execute(null).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(2);
        assertThat(testSubscriber.getOnNextEvents().get(0)).containsExactlyElementsOf(channelRealmList);

        assertThat(testSubscriber.getOnNextEvents().get(1)).hasSize(2);
        assertThat(testSubscriber.getOnNextEvents().get(1)).containsExactlyElementsOf(
                Arrays.asList(
                        new ChannelRealm(2, 10, "title2", "desc2", "image2", "url2", true),
                        new ChannelRealm(3, 11, "title3", "desc3", "image3", "url3", false)
                )
        );
    }
}