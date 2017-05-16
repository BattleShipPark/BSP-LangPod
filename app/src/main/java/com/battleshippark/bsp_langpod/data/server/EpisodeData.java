package com.battleshippark.bsp_langpod.data.server;

/**
 */

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class EpisodeData {
    public static EpisodeData create(String title, String desc, String url) {
        return new AutoValue_EpisodeData(title, desc, url);
    }

    public abstract String title();

    public abstract String desc();

    public abstract String url();
}
