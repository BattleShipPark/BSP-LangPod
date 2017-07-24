package com.battleshippark.bsp_langpod.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;

import java.io.File;
import java.io.IOException;

/**
 */

public class PlayerService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private HandlerThread thread;
    private Handler handler;
    private final MediaPlayer mp = new MediaPlayer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        thread = new HandlerThread(PlayerService.class.getSimpleName());
        thread.start();
        handler = new Handler(thread.getLooper());
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        thread.interrupt();
        return false;
    }

    public void play(EpisodeRealm episode) {
        handler.post(() -> {
            try {
                mp.setDataSource("file://" + episode.getDownloadedPath());
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void pause(EpisodeRealm episode) {
        handler.post(mp::pause);
    }

    public class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }
}
