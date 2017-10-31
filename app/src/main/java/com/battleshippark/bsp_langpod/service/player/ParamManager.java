package com.battleshippark.bsp_langpod.service.player;

import android.content.Context;
import android.content.Intent;

/**
 */

class ParamManager {
    private static final String KEY_CHANNEL_ID = "keyChannelId";
    private static final String KEY_EPISODE_ID = "keyEpisodeId";
    private static final String KEY_CURRENT_POSITION = "keyCurrentPosition";

    boolean hasPlayAction(Intent intent) {
        return checkIntent(intent, PlayerService.ACTION_PLAY);
    }

    boolean hasPauseAction(Intent intent) {
        return checkIntent(intent, PlayerService.ACTION_PAUSE);
    }

    boolean hasPlayedAction(Intent intent) {
        return checkIntent(intent, PlayerService.ACTION_PLAYED);
    }

    boolean hasPlayingAction(Intent intent) {
        return checkIntent(intent, PlayerService.ACTION_PLAYING);
    }

    boolean hasServiceIntent(Intent intent) {
        return checkIntent(intent, PlayerService.ACTION_PLAY) || checkIntent(intent, PlayerService.ACTION_PAUSE);
    }

    private boolean checkIntent(Intent intent, String action) {
        return intent != null && intent.getAction() != null
                && intent.getAction().equals(action);
    }

    Intent getPlayIntent(long episodeId) {
        Intent intent = new Intent(PlayerService.ACTION_PLAY);
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        return intent;
    }

    Intent getPauseIntent(long episodeId) {
        Intent intent = new Intent(PlayerService.ACTION_PAUSE);
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        return intent;
    }

    Intent getPlayedIntent(long episodeId) {
        Intent intent = new Intent(PlayerService.ACTION_PLAYED);
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        return intent;
    }

    Intent getPlayingIntent(long episodeId, long currentPosition) {
        Intent intent = new Intent(PlayerService.ACTION_PLAYING);
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        intent.putExtra(KEY_CURRENT_POSITION, currentPosition);
        return intent;
    }

    Intent getServiceIntent(Context context, boolean isPlaying, long channelId, long episodeId) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(isPlaying ? PlayerService.ACTION_PAUSE : PlayerService.ACTION_PLAY);
        intent.putExtra(KEY_CHANNEL_ID, channelId);
        intent.putExtra(KEY_EPISODE_ID, episodeId);
        return intent;
    }

    long getChannelId(Intent intent) {
        return intent.getLongExtra(KEY_CHANNEL_ID, -1);
    }

    long getEpisodeId(Intent intent) {
        return intent.getLongExtra(KEY_EPISODE_ID, -1);
    }

    long getCurrentPosition(Intent intent) {
        return intent.getLongExtra(KEY_CURRENT_POSITION, -1);
    }
}
