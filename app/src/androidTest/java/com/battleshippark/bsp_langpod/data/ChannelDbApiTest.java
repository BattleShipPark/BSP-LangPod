package com.battleshippark.bsp_langpod.data;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
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
    private EntireChannelRealm channelRealm1 = new EntireChannelRealm(1, 10, "title1", "desc1", "image1");
    private EntireChannelRealm channelRealm2 = new EntireChannelRealm(2, 11, "title2", "desc2", "image2");

    private Realm realm = Realm.getDefaultInstance();
    private ChannelDbRepository repository = new ChannelDbApi(realm);
    private TestSubscriber<List<EntireChannelRealm>> testSubscriber = new TestSubscriber<>();

    @Test
    public void entireChannelList_저장한것을읽어본다() {
        realm.executeTransaction(realm1 -> {
            realm1.delete(EntireChannelRealm.class);
            realm1.copyToRealm(channelRealm1);
            realm1.copyToRealm(channelRealm2);
        });


        repository.entireChannelList().subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);
        List<EntireChannelRealm> actualEntireChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));
        assertThat(actualEntireChannelRealmList.get(0)).isEqualTo(channelRealm1);
        assertThat(actualEntireChannelRealmList.get(1)).isEqualTo(channelRealm2);
    }

    @Test
    public void entireChannelList_읽은후에저장하면자동반영된다() {
        realm.executeTransaction(realm1 -> {
            realm1.delete(EntireChannelRealm.class);
            realm1.copyToRealm(channelRealm1);
        });


        repository.entireChannelList().subscribe(testSubscriber);


        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();


        assertThat(testSubscriber.getOnNextEvents()).hasSize(1);

        //지금은 한 건만 존재한다
        List<EntireChannelRealm> actualEntireChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));
        assertThat(actualEntireChannelRealmList).hasSize(1);
        assertThat(actualEntireChannelRealmList.get(0)).isEqualTo(channelRealm1);


        realm.executeTransaction(realm1 -> {
            realm1.copyToRealm(channelRealm2);//한 건 추가하면
        });
        //지금은 두 건 존재한다
        actualEntireChannelRealmList = realm.copyFromRealm(testSubscriber.getOnNextEvents().get(0));
        assertThat(actualEntireChannelRealmList).hasSize(2);
        assertThat(actualEntireChannelRealmList.get(0)).isEqualTo(channelRealm1);
        assertThat(actualEntireChannelRealmList.get(1)).isEqualTo(channelRealm2);
    }

    @Test
    public void myChannelList() {
        MyChannelRealm myChannelRealm1 = new MyChannelRealm(1, 10, "title1", "desc1", "cr1", "image1", "url1",
                new RealmList<>(new EpisodeRealm("ep.title1", "ep.desc1", "ep.url1")));
        MyChannelRealm myChannelRealm2 = new MyChannelRealm(2, 11, "title2", "desc2", "cr2", "image2", "url2",
                new RealmList<>(new EpisodeRealm("ep.title2", "ep.desc2", "ep.url2")));

        realm.executeTransaction(realm1 -> {
            realm1.delete(MyChannelRealm.class);
            realm1.copyToRealm(myChannelRealm1);
            realm1.copyToRealm(myChannelRealm2);
        });

        TestSubscriber<List<MyChannelRealm>> subscriber = new TestSubscriber<>();


        repository.myChannelList().subscribe(subscriber);


        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertCompleted();

        assertThat(subscriber.getOnNextEvents()).hasSize(1);
        List<MyChannelRealm> actualMyChannelRealmList = subscriber.getOnNextEvents().get(0);
        actualMyChannelRealmList = realm.copyFromRealm(actualMyChannelRealmList);

        assertThat(actualMyChannelRealmList).containsExactlyElementsOf(Arrays.asList(myChannelRealm1, myChannelRealm2));
    }

    @Test
    public void putEntireChannelList() {
        List<EntireChannelRealm> entireChannelRealmList = Arrays.asList(
                new EntireChannelRealm(1, 10, "title1", "desc1", "image1"),
                new EntireChannelRealm(2, 11, "title2", "desc2", "image2")
        );
        repository.putEntireChannelList(entireChannelRealmList);

        List<EntireChannelRealm> actualEntireChannelRealmList = repository.entireChannelList().toBlocking().single();
        actualEntireChannelRealmList = realm.copyFromRealm(actualEntireChannelRealmList);


        assertThat(actualEntireChannelRealmList).containsExactlyElementsOf(entireChannelRealmList);
    }
}