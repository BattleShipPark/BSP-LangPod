package com.battleshippark.bsp_langpod.service.downloader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.battleshippark.bsp_langpod.AppPhase;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.DownloadRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadCompleteParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadErrorParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;

import rx.functions.Action1;

/**
 */

public class Downloader {
    private final Context context;
    private final AppPhase appPhase;
    private final LocalServiceConnection connection = new LocalServiceConnection();
    private final DownloaderQueueManager queueManager = DownloaderQueueManager.getInstance();
    private boolean bound;

    public Downloader(Context context, AppPhase appPhase) {
        this.context = context;
        this.appPhase = appPhase;
    }

    public void enqueue(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        if (isBound()) {
            queueManager.offer(DownloadRealm.of(channelRealm, episodeRealm));
        } else {
            context.bindService(new Intent(context, DownloaderService.class), connection, Context.BIND_AUTO_CREATE);
        }
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
        intentFilter.addAction(DownloaderService.ACTION_PROGRESS);
        intentFilter.addAction(DownloaderService.ACTION_COMPLETED);
        intentFilter.addAction(DownloaderService.ACTION_ERROR);
        return intentFilter;
    }

    public DownloadProgressParam getProgressParam(Intent intent) {
        return connection.getService().getProgressParam(intent);
    }

    public DownloadCompleteParam getCompleteParam(Intent intent) {
        return connection.getService().getCompleteParam(intent);
    }

    public DownloadErrorParam getErrorParam(Intent intent) {
        return connection.getService().getErrorParam(intent);
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
