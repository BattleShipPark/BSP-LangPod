package com.battleshippark.bsp_langpod.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;

import rx.functions.Action1;

/**
 */

public class PlayerServiceFacade {
    private final Context context;
    private final LocalServiceConnection connection = new LocalServiceConnection();
    private boolean bound;

    public PlayerServiceFacade(Context context) {
        this.context = context;
    }

    public void play(EpisodeRealm episode) {
        if (isBound()) {
            connection.getService().play(episode);
        } else {
            connection.setOnConnected(service -> service.play(episode));
            context.bindService(new Intent(context, PlayerService.class), connection, 0);
        }
    }

    public void pause(EpisodeRealm episode) {
        if (isBound()) {
            connection.getService().pause(episode);
        } else {
            connection.setOnConnected(service -> service.pause(episode));
            context.bindService(new Intent(context, PlayerService.class), connection, 0);
        }
    }

    public void onStart() {
        if (!isBound()) {
            context.bindService(new Intent(context, PlayerService.class), connection, 0);
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
            bound = true;
            this.service = ((PlayerService.LocalBinder) service).getService();
            if (onConnected != null) {
                onConnected.call(this.service);
                onConnected = null;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }

        PlayerService getService() {
            return service;
        }

        void setOnConnected(Action1<PlayerService> onConnected) {
            this.onConnected = onConnected;
        }
    }
}
