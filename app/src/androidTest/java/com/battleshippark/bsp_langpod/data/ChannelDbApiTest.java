package com.battleshippark.bsp_langpod.data;

import org.junit.Test;

import io.realm.Realm;

/**
 */
public class ChannelDbApiTest {
    @Test
    public void entireChannelList_Realm() {
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

        repository.entireChannel().subscribe(entireChannelRealms -> {
            for (EntireChannelRealm result : entireChannelRealms) {
                System.out.println(result);
            }
        });
    }
}