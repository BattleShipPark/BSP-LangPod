package com.battleshippark.bsp_langpod.service.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
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

    public void play(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        if (isBound()) {
            connection.getService().play(channelRealm, episodeRealm);
        } else {
            connection.setOnConnected(service -> service.play(channelRealm, episodeRealm));
            context.bindService(new Intent(context, PlayerService.class), connection, 0);
        }
    }

    public void pause(ChannelRealm channelRealm, EpisodeRealm episode) {
        if (isBound()) {
            connection.getService().pause(channelRealm, episode);
        } else {
            connection.setOnConnected(service -> service.pause(channelRealm, episode));
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

    public IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_PLAY);
        intentFilter.addAction(PlayerService.ACTION_PAUSE);
        return intentFilter;
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
