package com.battleshippark.bsp_langpod.data.db;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 */

public class RealmHelperImpl implements RealmHelper {
    private Realm realm;

    public RealmHelperImpl(Realm realm) {
        this.realm = realm;
    }

    public long getNextEpisodeId() {
        long id;
        RealmQuery<MetaRealm> query = realm.where(MetaRealm.class);
        final MetaRealm metaRealm = query.findFirst();

        if (metaRealm == null) {
            id = 1;
        } else {
            id = metaRealm.getEpisodeId() + 1;
        }

        realm.executeTransaction(realm1 -> {
            if (metaRealm == null) {
                realm1.createObject(MetaRealm.class);
            } else {
                metaRealm.setEpisodeId(id);
            }
        });

        return id;
    }
}
