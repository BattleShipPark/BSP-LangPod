package com.battleshippark.bsp_langpod.presentation.entire_list;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

/**
 */

public interface OnItemListener {
    void onBindViewHolder(EntireChannelListAdapter.ViewHolder holder, ChannelRealm item);
}
