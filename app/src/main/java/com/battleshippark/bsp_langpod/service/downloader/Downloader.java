package com.battleshippark.bsp_langpod.service.downloader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.util.Logger;

/**
 */

public class Downloader {
    private static final String TAG = Downloader.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);
    private final Context context;
    private final LocalServiceConnection connection = new LocalServiceConnection();
    private final DownloaderQueueManager queueManager = DownloaderQueueManager.getInstance();

    public Downloader(Context context) {
        this.context = context;
    }

    public void enqueue(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        if (connection.isBound()) {
            queueManager.offer(channelRealm, episodeRealm);
        } else {
            context.bindService(new Intent(context, DownloaderService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void init() {
        if (!connection.isBound()) {
            context.bindService(new Intent(context, DownloaderService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void release() {
        if (connection.isBound()) {
            context.unbindService(connection);
        }
    }

    class LocalServiceConnection implements ServiceConnection {
        private DownloaderService service;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            this.service = ((DownloaderService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        DownloaderService getService() {
            return service;
        }

        boolean isBound() {
            return this.service != null && this.service.getBound();
        }
    }
}
