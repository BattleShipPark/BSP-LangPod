package com.battleshippark.bsp_langpod.data;

import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.battleshippark.bsp_langpod.data.db.ChannelDbApi;
import com.battleshippark.bsp_langpod.data.db.ChannelDbRepository;
import com.battleshippark.bsp_langpod.data.db.EntireChannelRealm;
import com.battleshippark.bsp_langpod.data.db.RealmHelper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class ChannelDbApiTest {
    @Test
    public void entireChannelList() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(realm1 -> {
            realm1.deleteAll();

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