package com.battleshippark.bsp_langpod.data.server;

/**
 */

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class EpisodeJson {
    public static EpisodeJson create(String title, String desc, String url, Date date) {
        return new AutoValue_EpisodeJson(title, desc, url, date);
    }

    public abstract String title();

    public abstract String desc();

    public abstract String url();

    public abstract Date date();
}
