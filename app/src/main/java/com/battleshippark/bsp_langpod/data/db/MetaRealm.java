package com.battleshippark.bsp_langpod.data.db;

import io.realm.RealmObject;

/**
 */

public class MetaRealm extends RealmObject {
    static final String ENTIRE_CHANNEL_ID = "entireChannelId";

    private long entireChannelId;
    private long episodeId = 1;

    public MetaRealm() {
    }

    public long getEntireChannelId() {
        return entireChannelId;
    }

    public void setEntireChannelId(long entireChannelId) {
        this.entireChannelId = entireChannelId;
    }

    public long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(long episodeId) {
        this.episodeId = episodeId;
    }
}
