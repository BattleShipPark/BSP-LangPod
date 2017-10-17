package com.battleshippark.bsp_langpod;

import android.app.Application;
import android.content.Intent;

import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.service.downloader.DownloaderQueueManager;
import com.battleshippark.bsp_langpod.service.downloader.DownloaderService;
import com.battleshippark.bsp_langpod.service.player.PlayerService;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;

/**
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        DownloaderQueueManager.create(DaggerDbApiGraph.create().downloadApi());

        startService(new Intent(this, PlayerService.class));
        startService(new Intent(this, DownloaderService.class));
    }
}
