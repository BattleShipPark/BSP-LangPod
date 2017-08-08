package com.battleshippark.bsp_langpod.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.util.Logger;

import java.io.IOException;

/**
 */

public class PlayerService extends Service {
    public static final String ACTION_PLAY = "actionPlay";
    public static final String ACTION_PAUSE = "actionPause";
    private static final String TAG = PlayerService.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);
    private static final MediaPlayer mp = new MediaPlayer();
    private static final Intent playIntent = new Intent(ACTION_PLAY);
    private static final Intent pauseIntent = new Intent(ACTION_PAUSE);
    private final IBinder mBinder = new LocalBinder();
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
        if (action != null) {
            if (action.equals(ACTION_PLAY)) {
                play();
            } else if (action.equals(ACTION_PAUSE)) {
                pause();
            }
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

    public void play() {
        handler.post(() -> {
            mp.start();
            sendBroadcast(playIntent);
        });
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

    public void pause() {
        handler.post(() -> {
            mp.pause();
            sendBroadcast(pauseIntent);
        });
    }

    public class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }
}
