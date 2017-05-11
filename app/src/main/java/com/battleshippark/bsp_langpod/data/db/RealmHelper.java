package com.battleshippark.bsp_langpod.data.db;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 */

public class RealmHelper {
    public static int getNextId(Realm realm, Class<? extends RealmObject> clazz) {
        try {
            return realm.where(clazz).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 1;
        }
    }
}
