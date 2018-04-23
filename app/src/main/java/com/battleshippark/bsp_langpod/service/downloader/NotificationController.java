package com.battleshippark.bsp_langpod.service.downloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

import com.battleshippark.bsp_langpod.Const;
import com.battleshippark.bsp_langpod.R;
import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

/**
 */

class NotificationController {
    private final Context context;
    private final int notificationId;
    private RemoteViews remoteViews;
    private Notification notification;
    private NotificationTarget notificationTarget;
    private boolean prepared;

    NotificationController(Context context, int notificationId) {
        this.context = context;
        this.notificationId = notificationId;

        create();
    }

    private void create() {
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
        remoteViews.setImageViewResource(R.id.image_iv, R.mipmap.ic_launcher);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.download)
                .setContent(remoteViews);

        notification = notificationBuilder.build();

        notificationTarget = new NotificationTarget(
                context,
                remoteViews,
                R.id.image_iv,
                notification,
                notificationId);
    }

    Notification prepare() {
        remoteViews.setImageViewResource(R.id.image_iv, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.channel_tv, context.getString(R.string.notification_waiting_download));
        remoteViews.setTextViewText(R.id.episode_tv, "");
        remoteViews.setTextViewText(R.id.progress_tv, context.getString(R.string.notification_downloading, 0f, 0f));

        prepared = true;

        return notification;
    }

    public void update(ChannelRealm channelRealm, EpisodeRealm episodeRealm, DownloadProgressParam param) {
        if (!prepared) {
            return;

        }
        if (param != null) {
            if (param.done()) {
                return;
            }
            remoteViews.setTextViewText(R.id.progress_tv,
                    context.getString(R.string.notification_downloading,
                            param.bytesRead() / Const.MEGA_BYTE, param.contentLength() / Const.MEGA_BYTE));
        }

        Glide.with(context).load(channelRealm.getImage()).asBitmap().into(notificationTarget);

        remoteViews.setTextViewText(R.id.channel_tv, channelRealm.getTitle());
        remoteViews.setTextViewText(R.id.episode_tv, episodeRealm.getTitle());

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notification);
    }

    void complete() {
        prepared = false;
    }
}
