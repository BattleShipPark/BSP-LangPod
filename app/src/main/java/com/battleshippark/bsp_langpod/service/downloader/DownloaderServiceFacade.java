package com.battleshippark.bsp_langpod.service.downloader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;

import java.io.File;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 */

public class DownloaderServiceFacade {
    private final Context context;
    private final PublishSubject<DownloadProgressParam> progressSubject;
    private final AppPhase appPhase;
    private final LocalServiceConnection connection = new LocalServiceConnection();
    private boolean bound;

    public DownloaderServiceFacade(Context context, PublishSubject<DownloadProgressParam> progressSubject, AppPhase appPhase) {
        this.context = context;
        this.progressSubject = progressSubject;
        this.appPhase = appPhase;
    }

/*    public Observable<File> download(String identifier, String url) {
        PublishSubject<File> resultSubject = PublishSubject.create();
        if (isBound()) {
            connection.getService().download(identifier, url, progressSubject, resultSubject);
        } else {
            connection.setOnConnected(service -> service.download(identifier, url, progressSubject, resultSubject));
            context.bindService(new Intent(context, PlayerService.class), connection, 0);
        }
        return resultSubject;
    }*/
    public Observable<File> download(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        PublishSubject<File> resultSubject = PublishSubject.create();
        if (isBound()) {
            connection.getService().download(channelRealm, episodeRealm, progressSubject, resultSubject);
        } else {
            connection.setOnConnected(service -> service.download(channelRealm, episodeRealm, progressSubject, resultSubject));
            context.bindService(new Intent(context, DownloaderService.class), connection, Context.BIND_AUTO_CREATE);
        }
        return resultSubject;
    }

    public void pause(ChannelRealm channelRealm, EpisodeRealm episode) {
        if (isBound()) {
//            connection.getService().pause(channelRealm, episode);
        } else {
//            connection.setOnConnected(service -> service.pause(channelRealm, episode));
            context.bindService(new Intent(context, DownloaderService.class), connection, 0);
        }
    }

    public void onStart() {
        if (!isBound()) {
            context.bindService(new Intent(context, DownloaderService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void onStop() {
        if (isBound()) {
            context.unbindService(connection);
        }
    }

    private boolean isBound() {
        return bound;
    }

    public IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(PlayerService.ACTION_PLAY);
//        intentFilter.addAction(PlayerService.ACTION_PAUSE);
        return intentFilter;
    }

    class LocalServiceConnection implements ServiceConnection {
        private DownloaderService service;
        private Action1<DownloaderService> onConnected;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bound = true;
            this.service = ((DownloaderService.LocalBinder) service).getService();
            if (onConnected != null) {
                onConnected.call(this.service);
                onConnected = null;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }

        DownloaderService getService() {
            return service;
        }

        void setOnConnected(Action1<DownloaderService> onConnected) {
            this.onConnected = onConnected;
        }
    }
}
