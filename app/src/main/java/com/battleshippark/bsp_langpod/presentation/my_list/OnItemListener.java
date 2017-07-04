package com.battleshippark.bsp_langpod.presentation.my_list;

import com.battleshippark.bsp_langpod.data.db.ChannelRealm;

/**
 */

interface OnItemListener {
    void onBindViewHolder(MyListAdapter.ViewHolder holder, ChannelRealm item);
}
