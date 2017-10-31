package com.battleshippark.bsp_langpod.service.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import rx.functions.Action1;
import rx.functions.Action2;

/**
 */

public class PlayerBroadcastReceiver extends BroadcastReceiver {
    private final Context context;
    private final Action1<Long> playAction;
    private final Action1<Long> pauseAction;
    private final Action1<Long> playedAction;
    private final Action2<Long, Long> playingAction;
    private final IntentFilter intentFilter;
    private final ParamManager paramManger;

    public PlayerBroadcastReceiver(Context context, Action1<Long> playAction, Action1<Long> pauseAction,
                                   Action1<Long> playedAction, Action2<Long, Long> playingAction) {
        this.context = context;
        this.playAction = playAction;
        this.pauseAction = pauseAction;
        this.playedAction = playedAction;
        this.playingAction = playingAction;
        this.intentFilter = createIntentFilter();
        this.paramManger = new ParamManager();
    }

    public void register() {
        context.registerReceiver(this, intentFilter);
    }

    public void unregister() {
        context.unregisterReceiver(this);
    }

    public IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_PLAY);
        intentFilter.addAction(PlayerService.ACTION_PAUSE);
        intentFilter.addAction(PlayerService.ACTION_PLAYED);
        intentFilter.addAction(PlayerService.ACTION_PLAYING);
        return intentFilter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (paramManger.hasPlayAction(intent)) {
            long episodeId = paramManger.getEpisodeId(intent);
            playAction.call(episodeId);
        } else if (paramManger.hasPauseAction(intent)) {
            long episodeId = paramManger.getEpisodeId(intent);
            pauseAction.call(episodeId);
        } else if (paramManger.hasPlayedAction(intent)) {
            long episodeId = paramManger.getEpisodeId(intent);
            playedAction.call(episodeId);
        } else if (paramManger.hasPlayingAction(intent)) {
            long episodeId = paramManger.getEpisodeId(intent);
            long currentPosition = paramManger.getCurrentPosition(intent);
            playingAction.call(episodeId, currentPosition);
        }
    }
}
