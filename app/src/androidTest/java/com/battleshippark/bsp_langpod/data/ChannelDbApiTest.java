package com.battleshippark.bsp_langpod.data;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.RealmConfigurationFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import rx.functions.Actions;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class ChannelDbApiTest {
    private ChannelRealm channelRealm1 = new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", "cr1",
            new RealmList<>(new EpisodeRealm(2, "ep.title1", "ep.desc1", "ep.url1", new Date(111))), false);
    private ChannelRealm channelRealm2 = new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", "cr2",
            new RealmList<>(new EpisodeRealm(1, "ep.title2", "ep.desc2", "ep.url2", new Date(222))), true);

    private Realm realm;
    private RealmConfiguration configuration;
    private ChannelDbRepository repository;
    private TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();

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
    public void entireChannelList_저장한것을읽어본다() {
        realm.executeTransaction(realm1 -> {
            realm1.copyToRealm(channelRealm1);
            realm1.copyToRealm(channelRealm2);
        });


        repository.entireChannelList().subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
        List<ChannelRealm> actualChannelRealmList = testSubscriber.getOnNextEvents().get(0);
        assertThat(actualChannelRealmList.get(0)).isEqualTo(channelRealm1);
        assertThat(actualChannelRealmList.get(1)).isEqualTo(channelRealm2);
    }

    @Test
    public void myChannelList() {
        //subscribed=true인 title2만 조회해야 한다
        realm.executeTransaction(realm1 -> {
            realm1.copyToRealm(channelRealm1);
            realm1.copyToRealm(channelRealm2);
        });

        TestSubscriber<List<ChannelRealm>> subscriber = new TestSubscriber<>();


        repository.myChannelList().subscribe(subscriber);


        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertCompleted();

        assertThat(subscriber.getOnNextEvents()).hasSize(1);
        List<ChannelRealm> actualMyChannelRealmList = subscriber.getOnNextEvents().get(0);
        assertThat(actualMyChannelRealmList).hasSize(1);
        assertThat(actualMyChannelRealmList.get(0)).isEqualTo(channelRealm2);
    }

    @Test
    public void channelWithEpisodeId() {
        realm.executeTransaction(realm1 -> {
            realm1.copyToRealm(channelRealm1);
            realm1.copyToRealm(channelRealm2);
        });

        TestSubscriber<ChannelRealm> subscriber = new TestSubscriber<>();


        repository.channelWithEpisodeId(2).subscribe(subscriber);


        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertCompleted();

        assertThat(subscriber.getOnNextEvents()).hasSize(1);
        ChannelRealm actualMyChannelRealm = subscriber.getOnNextEvents().get(0);
        assertThat(actualMyChannelRealm).isEqualTo(channelRealm1);
    }

    @Test
    public void putEntireChannelList() {
        List<ChannelRealm> channelRealmList = Arrays.asList(
                channelRealm1, channelRealm2
        );
        repository.putEntireChannelList(channelRealmList)
                .subscribe(Actions.empty(),
                        throwable -> {
                            throw new RuntimeException(throwable);
                        });

        TestSubscriber<List<ChannelRealm>> subscriber = new TestSubscriber<>();

        repository.entireChannelList().subscribe(subscriber);

        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertCompleted();


        List<ChannelRealm> actualChannelRealmList = subscriber.getOnNextEvents().get(0);


        assertThat(actualChannelRealmList).containsExactlyElementsOf(channelRealmList);
    }

    @Test
    public void putChannel() throws InterruptedException {
        ChannelDbRepository repository = new ChannelDbApi(configuration);

        repository.putChannel(channelRealm1).subscribe();

        TestSubscriber<ChannelRealm> testSubscriber = new TestSubscriber<>();
        repository.channel(1).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents().get(0)).isEqualTo(channelRealm1);
    }

    @Test
    public void putEpisode() throws InterruptedException {
        //ID1으로 저장해 놓고
        EpisodeRealm episodeRealm = new EpisodeRealm(1, "ep.title1", "ep.desc1", "ep.url1", new Date(111));
        realm.executeTransaction(realm1 -> {
            realm1.insert(episodeRealm);
        });

        EpisodeRealm actualEpisodeRealm = realm.copyFromRealm(realm.where(EpisodeRealm.class).findFirst());
        assertThat(actualEpisodeRealm).isEqualTo(episodeRealm);

        //객체 갱신
        episodeRealm.setTitle("ep.title2");

        repository.putEpisode(episodeRealm).subscribe();

        //title이 수정되어 있는걸 확인
        actualEpisodeRealm = realm.copyFromRealm(realm.where(EpisodeRealm.class).findFirst());
        assertThat(actualEpisodeRealm.getTitle()).isEqualTo("ep.title2");
    }

    @Test
    public void subscribeChannel_구독상태를해지() {
        //구독상태의 채널 저장
        realm.executeTransaction(realm1 -> {
            realm1.copyToRealm(channelRealm2);
        });

        //구독 해지
        TestSubscriber<Boolean> testSubscriber1 = new TestSubscriber<>();
        repository.switchSubscribe(channelRealm2).subscribe(testSubscriber1);

        testSubscriber1.awaitTerminalEvent();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertCompleted();


        // 읽어 본다
        TestSubscriber<ChannelRealm> testSubscriber2 = new TestSubscriber<>();
        repository.channel(2).subscribe(testSubscriber2);

        testSubscriber2.awaitTerminalEvent();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertCompleted();


        ChannelRealm actualChannelRealm = testSubscriber2.getOnNextEvents().get(0);
        assertThat(actualChannelRealm.getId()).isEqualTo(2);

        //해지 되어 있는걸 확인
        assertThat(actualChannelRealm.isSubscribed()).isEqualTo(false);
    }
}