package com.battleshippark.bsp_langpod.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;

import java.io.IOException;

/**
 */

public class PlayerService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private HandlerThread thread;
    private Handler handler;
    private final MediaPlayer mp = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();

        thread = new HandlerThread(PlayerService.class.getSimpleName());
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.interrupt();
    }

    public void play(EpisodeRealm episode) {
        handler.post(() -> {
            try {
                mp.stop();
                mp.reset();
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
