package com.battleshippark.bsp_langpod;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;

import com.battleshippark.bsp_langpod.dagger.DaggerDbApiGraph;
import com.battleshippark.bsp_langpod.dagger.DaggerDomainMapperGraph;
import com.battleshippark.bsp_langpod.service.downloader.DownloaderQueueManager;
import com.battleshippark.bsp_langpod.service.downloader.DownloaderService;
import com.battleshippark.bsp_langpod.service.player.PlayerService;
import com.battleshippark.bsp_langpod.util.StethoHelper;
import com.squareup.leakcanary.LeakCanary;

import io.realm.Realm;

/**
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        StethoHelper.initialize(this);

        DownloaderQueueManager.create(DaggerDbApiGraph.create().downloadApi(),
                DaggerDomainMapperGraph.create().domainMapper());

        startService(new Intent(this, PlayerService.class));
        startService(new Intent(this, DownloaderService.class));

        StrictMode.enableDefaults();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
