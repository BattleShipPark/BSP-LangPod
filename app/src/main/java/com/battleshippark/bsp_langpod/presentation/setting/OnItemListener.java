package com.battleshippark.bsp_langpod.presentation.setting;

import com.battleshippark.bsp_langpod.data.db.DownloadRealm;

/**
 */

interface OnItemListener {
    void onBindViewHolder(DownloadListAdapter.ViewHolder holder, DownloadRealm downloadRealm);

}
