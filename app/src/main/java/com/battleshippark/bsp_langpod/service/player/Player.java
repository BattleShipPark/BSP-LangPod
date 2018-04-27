package com.battleshippark.bsp_langpod.service.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;

import rx.functions.Action1;

/**
 */

public class Player {
    private final Context context;
    private final LocalServiceConnection connection = new LocalServiceConnection();
    private boolean bound;

    public Player(Context context) {
        this.context = context;
    }

    public void play(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        if (isBound()) {
            connection.getService().play(channelRealm, episodeRealm);
        } else {
            connection.setOnConnected(service -> service.play(channelRealm, episodeRealm));
            context.bindService(new Intent(context, PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void pause(ChannelRealm channelRealm, EpisodeRealm episode) {
        if (isBound()) {
            connection.getService().pause(channelRealm, episode);
        } else {
            connection.setOnConnected(service -> service.pause(channelRealm, episode));
            context.bindService(new Intent(context, PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void onStart() {
        if (!isBound()) {
            context.bindService(new Intent(context, PlayerService.class), connection, Context.BIND_AUTO_CREATE);
            bound = true;
        }
    }

    public void onStop() {
        if (isBound()) {
            context.unbindService(connection);
            bound = false;
        }
    }

    private boolean isBound() {
        return bound;
    }

    private class LocalServiceConnection implements ServiceConnection {
        private PlayerService service;
        private Action1<PlayerService> onConnected;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            this.service = ((PlayerService.LocalBinder) service).getService();
            if (onConnected != null) {
                onConnected.call(this.service);
                onConnected = null;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            this.service = null;
        }

        PlayerService getService() {
            return service;
        }

        void setOnConnected(Action1<PlayerService> onConnected) {
            this.onConnected = onConnected;
        }
    }
}
