package com.battleshippark.bsp_langpod.data.db;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 */

public class RealmHelper {
    public static int getNextId(Realm realm, Class<? extends RealmObject> clazz) {
        Number number = realm.where(clazz).max("id");
        if (number == null) {
            return 1;
        } else {
            return number.intValue() + 1;
        }
    }

    public static long getNextEntireChannelId(Realm realm) {
        long id;
        RealmQuery<MetaRealm> query = realm.where(MetaRealm.class);
        MetaRealm metaRealm = query.findFirst();

        if (metaRealm == null) {
            id = 1;
            metaRealm = realm.createObject(MetaRealm.class);
        } else {
            id = metaRealm.getEntireChannelId() + 1;
        }
        metaRealm.setEntireChannelId(id);

        return id;
    }

    public static void deleteAll(Realm realm) {
        realm.delete(ChannelRealm.class);
        realm.delete(MyChannelRealm.class);
        realm.delete(EpisodeRealm.class);
    }
}
