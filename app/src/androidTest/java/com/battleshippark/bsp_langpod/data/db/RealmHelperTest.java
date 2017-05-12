package com.battleshippark.bsp_langpod.data.db;

import org.junit.Test;

import io.realm.Realm;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class RealmHelperTest {
    @Test
    public void getNextEntireChannelId_기본값1() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            realm1.deleteAll();
            long id = RealmHelper.getNextEntireChannelId(realm1);
            assertThat(id).isEqualTo(1);
        });
    }

    @Test
    public void getNextEntireChannelId_잘증가하는지() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            realm1.deleteAll();

            RealmHelper.getNextEntireChannelId(realm1);
            long id = RealmHelper.getNextEntireChannelId(realm1);
            assertThat(id).isEqualTo(2);
        });
    }

    @Test
    public void getNextEntireChannelId_삭제후에도연속성이있는지() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            realm1.deleteAll();

            RealmHelper.getNextEntireChannelId(realm1);
            RealmHelper.getNextEntireChannelId(realm1);

            RealmHelper.deleteAll(realm1);
            long id = RealmHelper.getNextEntireChannelId(realm1);
            assertThat(id).isEqualTo(3);
        });
    }
}