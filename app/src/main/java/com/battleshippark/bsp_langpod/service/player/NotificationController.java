package com.battleshippark.bsp_langpod.service.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

/**
 */

class NotificationController {
    private final Context context;
    private final ParamManager paramManager;
    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationTarget notificationTarget;
    private boolean prepared;

    NotificationController(Context context) {
        this.context = context;
        this.paramManager = new ParamManager();

        create();
    }

    private void create() {
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_play);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.download)
                .setContent(remoteViews);

        notification = notificationBuilder.build();
    }

    Notification prepare() {
        remoteViews.setImageViewResource(R.id.image_iv, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.channel_tv, "");
        remoteViews.setTextViewText(R.id.episode_tv, "");
        remoteViews.setImageViewResource(R.id.play_iv, R.drawable.play);

        prepared = true;

        return notification;
    }

    public void update(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        if (!prepared) {
            return;
        }

        notificationTarget = new NotificationTarget(
                context,
                remoteViews,
                R.id.image_iv,
                notification,
                (int) episodeRealm.getId());
        Glide.with(context).load(channelRealm.getImage()).asBitmap().into(notificationTarget);

        PendingIntent pendingIntent = createPendingIntent(channelRealm.getId(), episodeRealm.getId(), episodeRealm.getPlayState());
        remoteViews.setOnClickPendingIntent(R.id.play_iv, pendingIntent);
        remoteViews.setTextViewText(R.id.channel_tv, channelRealm.getTitle());
        remoteViews.setTextViewText(R.id.episode_tv, episodeRealm.getTitle());
        if (episodeRealm.getPlayState() == EpisodeRealm.PlayState.PLAYING) {
            remoteViews.setImageViewResource(R.id.play_iv, R.drawable.pause);
        } else if (episodeRealm.getPlayState() == EpisodeRealm.PlayState.PAUSE || episodeRealm.getPlayState() == EpisodeRealm.PlayState.PLAYED) {
            remoteViews.setImageViewResource(R.id.play_iv, R.drawable.play);
        }

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify((int) episodeRealm.getId(), notification);
    }

    private PendingIntent createPendingIntent(long channelId, long episodeId, EpisodeRealm.PlayState state) {
        Intent intent = paramManager.getServiceIntent(context, state, channelId, episodeId);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    void complete() {
        prepared = false;
    }
}
