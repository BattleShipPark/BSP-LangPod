package com.battleshippark.bsp_langpod.presentation.channel;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;
import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;

/**
 */

interface OnItemListener {
    void onBindHeaderViewHolder(ChannelAdapter.HeaderViewHolder holder, ChannelRealm channel);

    void onBindEpisodeViewHolder(ChannelAdapter.EpisodeViewHolder holder, EpisodeRealm episode);
}
