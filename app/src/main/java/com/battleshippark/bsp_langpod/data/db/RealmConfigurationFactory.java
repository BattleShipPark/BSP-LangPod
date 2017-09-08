package com.battleshippark.bsp_langpod.data.db;

import io.realm.RealmConfiguration;

/**
 */

public class RealmConfigurationFactory {
    public static RealmConfiguration create() {
        return new RealmConfiguration.Builder().schemaVersion(1).build();
    }

    public static RealmConfiguration createTest() {
        return new RealmConfiguration.Builder().name("test.realm").inMemory().build();
    }
}
