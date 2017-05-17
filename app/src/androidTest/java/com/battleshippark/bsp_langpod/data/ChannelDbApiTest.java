package com.battleshippark.bsp_langpod.data;

import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.db.MyChannelRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;

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
    @Test
    public void entireChannelList() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            realm1.delete(EntireChannelRealm.class);

            EntireChannelRealm channelRealm = realm1.createObject(EntireChannelRealm.class);
            channelRealm.setId(RealmHelper.getNextId(realm1, EntireChannelRealm.class));
            channelRealm.setTitle("title1");
            channelRealm.setDesc("desc1");
            channelRealm.setImage("image1");

            channelRealm = realm1.createObject(EntireChannelRealm.class);
            channelRealm.setId(RealmHelper.getNextId(realm1, EntireChannelRealm.class));
            channelRealm.setTitle("title2");
            channelRealm.setDesc("desc2");
            channelRealm.setImage("image2");
        });

        ChannelDbRepository repository = new ChannelDbApi(realm);

        repository.entireChannelList().subscribe(entireChannelRealms -> {
            for (EntireChannelRealm result : entireChannelRealms) {
                System.out.println(result);
            }
        });
    }

    @Test
    public void myChannelList() {
        Realm realm = Realm.getDefaultInstance();

        MyChannelRealm myChannelRealm1 = new MyChannelRealm(1, 10, "title1", "desc1", "cr1", "image1", "url1",
                new RealmList<>(new EpisodeRealm("ep.title1", "ep.desc1", "ep.url1")));
        MyChannelRealm myChannelRealm2 = new MyChannelRealm(2, 11, "title2", "desc2", "cr2", "image2", "url2",
                new RealmList<>(new EpisodeRealm("ep.title2", "ep.desc2", "ep.url2")));

        realm.executeTransaction(realm1 -> {
            realm1.delete(MyChannelRealm.class);
            realm1.copyToRealm(myChannelRealm1);
            realm1.copyToRealm(myChannelRealm2);
        });

        ChannelDbRepository repository = new ChannelDbApi(realm);
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
        Realm realm = Realm.getDefaultInstance();
        ChannelDbRepository repository = new ChannelDbApi(realm);
        repository.putEntireChannelList(entireChannelRealmList);

        List<EntireChannelRealm> actualEntireChannelRealmList = repository.entireChannelList().toBlocking().single();
        actualEntireChannelRealmList = realm.copyFromRealm(actualEntireChannelRealmList);


        assertThat(actualEntireChannelRealmList).containsExactlyElementsOf(entireChannelRealmList);
    }
}