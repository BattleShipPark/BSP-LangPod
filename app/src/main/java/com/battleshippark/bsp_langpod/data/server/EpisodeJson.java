package com.battleshippark.bsp_langpod.data.server;

/**
 */

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class EpisodeJson {
    public static EpisodeJson create(String title, String desc, String url, long length, Date date) {
        return new AutoValue_EpisodeJson(title, desc, url, length, date);
    }

    public abstract String title();

    public abstract String desc();

    public abstract String url();

    public abstract long length();

    public abstract Date date();
}
