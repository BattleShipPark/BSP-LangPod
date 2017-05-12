package com.battleshippark.bsp_langpod.data.db;

import io.realm.RealmObject;

/**
 */

public class MetaRealm extends RealmObject {
    static final String ENTIRE_CHANNEL_ID = "entireChannelId";

    private long entireChannelId;

    public MetaRealm() {
    }

    public long getEntireChannelId() {
        return entireChannelId;
    }

    public void setEntireChannelId(long entireChannelId) {
        this.entireChannelId = entireChannelId;
    }
}
