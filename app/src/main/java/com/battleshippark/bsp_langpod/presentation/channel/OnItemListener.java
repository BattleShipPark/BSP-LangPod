package com.battleshippark.bsp_langpod.presentation.channel;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

/**
 */

interface OnItemListener {
    void onBindHeaderViewHolder(ChannelAdapter.HeaderViewHolder holder, ChannelRealm item);

    void onBindEpisodeViewHolder(ChannelAdapter.EpisodeViewHolder holder, ChannelRealm item);
}
