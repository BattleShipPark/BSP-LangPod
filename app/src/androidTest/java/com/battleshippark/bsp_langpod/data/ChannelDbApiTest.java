package com.battleshippark.bsp_langpod.data;

import android.os.Handler;
import android.os.HandlerThread;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;

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
            new RealmList<>(new EpisodeRealm(2, "ep.title1", "ep.desc1", "ep.url1", 11, new Date(111))), false);
    private ChannelRealm channelRealm2 = new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", "cr2",
            new RealmList<>(new EpisodeRealm(1, "ep.title2", "ep.desc2", "ep.url2", 22, new Date(222))), true);

    private Realm realm;
    private ChannelDbRepository repository;
    private TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();

    @Before
    public void before() {
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("test.realm").build();
        Realm.deleteRealm(configuration);
        realm = Realm.getInstance(configuration);
        repository = new ChannelDbApi(realm);
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
        List<ChannelRealm> actualChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));
        assertThat(actualChannelRealmList.get(0)).isEqualTo(channelRealm1);
        assertThat(actualChannelRealmList.get(1)).isEqualTo(channelRealm2);
    }

    @Test
    public void entireChannelList_읽은후에저장하면자동반영된다() {
        realm.executeTransaction(realm1 -> {
            realm1.copyToRealm(channelRealm1);
        });


        repository.entireChannelList().subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        //지금은 한 건만 존재한다
        List<ChannelRealm> actualChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));
        assertThat(actualChannelRealmList).hasSize(1);
        assertThat(actualChannelRealmList.get(0)).isEqualTo(channelRealm1);


        realm.executeTransaction(realm1 -> {
            realm1.copyToRealm(channelRealm2);//한 건 추가하면
        });
        //지금은 두 건 존재한다
        actualChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));
        assertThat(actualChannelRealmList).hasSize(2);
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

        List<ChannelRealm> actualChannelRealmList = repository.entireChannelList().toBlocking().single();
        actualChannelRealmList = realm.copyFromRealm(actualChannelRealmList);


        assertThat(actualChannelRealmList).containsExactlyElementsOf(channelRealmList);
    }

    @Test
    public void putChannel() throws InterruptedException {
        HandlerThread handlerThread = new HandlerThread("ChannelDbApiTest");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            ChannelDbRepository repository = new ChannelDbApi(realm);

            //ID1으로 저장해 놓고
            ChannelRealm channelRealm = new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", "cr1",
                    new RealmList<>(new EpisodeRealm(1, "ep.title1", "ep.desc1", "ep.url1", 11, new Date(111))), false);
            realm.executeTransaction(realm1 -> {
                realm1.copyToRealm(channelRealm);
            });

            TestSubscriber<ChannelRealm> testSubscriber = new TestSubscriber<>();
            //ID1을 읽어 보면
            repository.channel(1).subscribe(testSubscriber);


            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoErrors();
            testSubscriber.assertCompleted();

            ChannelRealm actualChannelRealm1 = testSubscriber.getOnNextEvents().get(0);
            assertThat(actualChannelRealm1.getTitle()).isEqualTo("title1");
            assertThat(actualChannelRealm1.getEpisodes()).hasSize(1);
            assertThat(actualChannelRealm1.getEpisodes().get(0).getTitle()).isEqualTo("ep.title1");

            //객체 갱신
            channelRealm.setTitle("title2");
            channelRealm.getEpisodes().add(new EpisodeRealm(2, "ep.title2", "ep.desc2", "ep,url2", 22, new Date(222)));

            repository.putChannel(channelRealm);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //title과 items가 수정되어 있는걸 확인
            assertThat(actualChannelRealm1.getTitle()).isEqualTo("title2");
            assertThat(actualChannelRealm1.getEpisodes()).hasSize(2);
            assertThat(actualChannelRealm1.getEpisodes().get(0).getTitle()).isEqualTo("ep.title1");
            assertThat(actualChannelRealm1.getEpisodes().get(1).getTitle()).isEqualTo("ep.title2");

            //에피소드가 2개인 것을 확인
            repository.channel(1).subscribe(testSubscriber);
            assertThat(testSubscriber.getOnNextEvents().get(0).getEpisodes()).hasSize(2);
        });
    }

    @Test
    public void putEpisode() throws InterruptedException {
        //ID1으로 저장해 놓고
        EpisodeRealm episodeRealm = new EpisodeRealm(1, "ep.title1", "ep.desc1", "ep.url1", 11, new Date(111));
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
        HandlerThread handlerThread = new HandlerThread("ChannelDbApiTest");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            ChannelDbRepository repository = new ChannelDbApi(realm);

            //구독상태의 채널 저장
            realm.executeTransaction(realm1 -> {
                realm1.copyToRealm(channelRealm2);
            });

            TestSubscriber<ChannelRealm> testSubscriber = new TestSubscriber<>();
            repository.channel(2).subscribe(testSubscriber);


            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoErrors();
            testSubscriber.assertCompleted();

            ChannelRealm actualChannelRealm1 = testSubscriber.getOnNextEvents().get(0);
            assertThat(actualChannelRealm1.getId()).isEqualTo(2);


            //구독 해지
            TestSubscriber<Void> testSubscriber2 = new TestSubscriber<>();
            repository.switchSubscribe(channelRealm1).subscribe(testSubscriber2);

            testSubscriber2.awaitTerminalEvent();
            testSubscriber2.assertNoErrors();
            testSubscriber2.assertCompleted();

            //해지 되어 있는걸 확인
            assertThat(actualChannelRealm1.isSubscribed()).isEqualTo(false);
        });
    }
}