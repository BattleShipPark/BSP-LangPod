package com.battleshippark.bsp_langpod.data;

import android.os.Handler;
import android.os.HandlerThread;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class ChannelDbApiTest {
    private ChannelRealm channelRealm1 = new ChannelRealm(1, 10, "title1", "desc1", "image1", "url1", false);
    private ChannelRealm channelRealm2 = new ChannelRealm(2, 11, "title2", "desc2", "image2", "url2", true);

    private Realm realm = Realm.getDefaultInstance();
    private ChannelDbRepository repository = new ChannelDbApi(realm);
    private TestSubscriber<List<ChannelRealm>> testSubscriber = new TestSubscriber<>();

    @Test
    public void entireChannelList_저장한것을읽어본다() {
        realm.executeTransaction(realm1 -> {
            realm1.delete(ChannelRealm.class);
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
            realm1.delete(ChannelRealm.class);
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
            realm1.delete(ChannelRealm.class);
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
        actualMyChannelRealmList = realm.copyFromRealm(actualMyChannelRealmList);
        assertThat(actualMyChannelRealmList).hasSize(1);
        assertThat(actualMyChannelRealmList.get(0)).isEqualTo(channelRealm2);
    }

    @Test
    public void putEntireChannelList() {
        List<ChannelRealm> channelRealmList = Arrays.asList(
                channelRealm1, channelRealm2
        );
        repository.putEntireChannelList(channelRealmList);

        List<ChannelRealm> actualChannelRealmList = repository.entireChannelList().toBlocking().single();
        actualChannelRealmList = realm.copyFromRealm(actualChannelRealmList);


        assertThat(actualChannelRealmList).containsExactlyElementsOf(channelRealmList);
    }

    @Test
    public void putMyChannel() throws InterruptedException {
        HandlerThread handlerThread = new HandlerThread("ChannelDbApiTest");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            Realm realm = Realm.getDefaultInstance();
            ChannelDbRepository repository = new ChannelDbApi(realm);

            //ID1으로 저장해 놓고
            MyChannelRealm myChannelRealm = new MyChannelRealm(1, 10, "title1", "desc1", "cr1", "image1", "url1",
                    new RealmList<>(new EpisodeRealm("ep.title1", "ep.desc1", "ep.url1")));
            realm.executeTransaction(realm1 -> {
                realm1.delete(MyChannelRealm.class);
                realm1.copyToRealm(myChannelRealm);
            });

            TestSubscriber<MyChannelRealm> testSubscriber = new TestSubscriber<>();
            //ID1을 읽어 보면
            repository.myChannel(1).subscribe(testSubscriber);


            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoErrors();
            testSubscriber.assertCompleted();

            MyChannelRealm actualMyChannelRealm1 = testSubscriber.getOnNextEvents().get(0);
            assertThat(actualMyChannelRealm1.getTitle()).isEqualTo("title1");
            assertThat(actualMyChannelRealm1.getItems()).hasSize(1);
            assertThat(actualMyChannelRealm1.getItems().get(0).getTitle()).isEqualTo("ep.title1");

            //객체 갱신
            myChannelRealm.setTitle("title2");
            myChannelRealm.getItems().add(new EpisodeRealm("ep.title2", "ep.desc2", "ep,url2"));

            repository.putMyChannel(myChannelRealm);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //title과 items가 수정되어 있는걸 확인
            assertThat(actualMyChannelRealm1.getTitle()).isEqualTo("title2");
            assertThat(actualMyChannelRealm1.getItems()).hasSize(2);
            assertThat(actualMyChannelRealm1.getItems().get(1).getTitle()).isEqualTo("ep.title2");
        });
    }
}