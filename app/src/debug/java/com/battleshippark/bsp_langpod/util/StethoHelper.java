package com.battleshippark.bsp_langpod.util;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

public class StethoHelper {
    public static void initialize(Application app) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(app)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(app))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(app).build())
                        .build());
    }
}
