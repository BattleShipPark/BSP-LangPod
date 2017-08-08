package com.battleshippark.bsp_langpod.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.util.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.io.IOException;

/**
 */

public class PlayerService extends Service {
    public static final String ACTION_PLAY = "actionPlay";
    public static final String ACTION_PAUSE = "actionPause";
    public static final String KEY_EPISODE_ID = "keyEpisodeId";
    private static final String TAG = PlayerService.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);
    private static final MediaPlayer mp = new MediaPlayer();
    private static final Intent playIntent = new Intent(ACTION_PLAY);
    private static final Intent pauseIntent = new Intent(ACTION_PAUSE);
    private final IBinder mBinder = new LocalBinder();
    private HandlerThread thread;
    private Handler handler;
    private ChannelRealm channelRealm;
    private EpisodeRealm episodeRealm;

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

    private void play() {
        handler.post(() -> {
            mp.start();
            sendBroadcast(playIntent, episodeRealm.getId());
        });
    }

    public void play(ChannelRealm channelRealm, EpisodeRealm episode) {
        this.channelRealm = channelRealm;
        this.episodeRealm = episode;
        handler.post(() -> {
            try {
                mp.stop();
                mp.reset();
                mp.setDataSource("file://" + episodeRealm.getDownloadedPath());
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                logger.w(e);
            }
        });
        showNotification(channelRealm, episodeRealm, true);
    }

    public void pause() {
        handler.post(() -> {
            mp.pause();
            sendBroadcast(pauseIntent, episodeRealm.getId());
        });
        showNotification(channelRealm, episodeRealm, false);
    }

    private void showNotification(ChannelRealm channelRealm, EpisodeRealm episodeRealm, boolean isPlaying) {
        PendingIntent pendingIntent = createPendingIntent(isPlaying);

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification);
        rv.setImageViewResource(R.id.image_iv, R.mipmap.ic_launcher);
        rv.setTextViewText(R.id.channel_tv, channelRealm.getTitle());
        rv.setTextViewText(R.id.episode_tv, episodeRealm.getTitle());
        rv.setImageViewResource(R.id.play_iv, isPlaying ? R.drawable.pause : R.drawable.play);
        rv.setOnClickPendingIntent(R.id.play_iv, pendingIntent);

        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.download)
                .setContent(rv);
        final Notification notification = mBuilder.build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, notification);

        NotificationTarget notificationTarget = new NotificationTarget(
                this,
                rv,
                R.id.image_iv,
                notification,
                0);
        Glide.with(getApplicationContext()).load(channelRealm.getImage()).asBitmap().into(notificationTarget);
    }

    private PendingIntent createPendingIntent(boolean isPlaying) {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(isPlaying ? PlayerService.ACTION_PAUSE : PlayerService.ACTION_PLAY);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendBroadcast(Intent intent, long episodeId) {
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }
}
