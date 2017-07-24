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

    public PlayerServiceFacade(Context context) {
        this.context = context;
    }

    public void play(EpisodeRealm episode) {
        if (connection.isBound()) {
            connection.getService().play(episode);
        } else {
            connection.setOnConnected(service -> service.play(episode));
            context.bindService(new Intent(context, PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void pause(EpisodeRealm episode) {
        if (connection.isBound()) {
            connection.getService().pause(episode);
        } else {
            connection.setOnConnected(service -> service.pause(episode));
            context.bindService(new Intent(context, PlayerService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    private class LocalServiceConnection implements ServiceConnection {
        private boolean bound;
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

        boolean isBound() {
            return bound;
        }

        PlayerService getService() {
            return service;
        }

        void setOnConnected(Action1<PlayerService> onConnected) {
            this.onConnected = onConnected;
        }
    }
}
