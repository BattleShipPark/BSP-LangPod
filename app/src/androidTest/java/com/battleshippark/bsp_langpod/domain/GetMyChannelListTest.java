package com.battleshippark.bsp_langpod.domain;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.RealmConfigurationFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class GetMyChannelListTest {
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
        List<ChannelRealm> channelRealmList = Arrays.asList(
                new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", false),
                new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", true)
        );
        repository.putEntireChannelList(channelRealmList).subscribe();


        GetMyChannelList getMyChannelList = new GetMyChannelList(repository, Schedulers.immediate(), Schedulers.immediate());
        TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();


        getMyChannelList.execute(null).subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
        assertThat(testSubscriber.getOnNextEvents().get(0)).hasSize(1);

        //isSubscribed=true인 title2만 나와야 한다
        assertThat(testSubscriber.getOnNextEvents().get(0).get(0)).isEqualTo(channelRealmList.get(1));
    }
}