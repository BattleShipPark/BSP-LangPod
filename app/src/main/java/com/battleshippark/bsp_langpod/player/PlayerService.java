package com.battleshippark.bsp_langpod.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.util.Logger;

import java.io.IOException;

/**
 */

public class PlayerService extends Service {
    private static final String TAG = PlayerService.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);
    private final IBinder mBinder = new LocalBinder();
    private final MediaPlayer mp = new MediaPlayer();
    private HandlerThread thread;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        thread = new HandlerThread(PlayerService.class.getSimpleName());
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if (action != null && action.equals("action")) {
            Toast.makeText(this, "NOTIFICATION", Toast.LENGTH_SHORT).show();
        }
        return ret;
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
                logger.w(e);
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
