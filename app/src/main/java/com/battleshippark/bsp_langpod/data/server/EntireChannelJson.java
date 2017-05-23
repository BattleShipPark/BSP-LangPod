package com.battleshippark.bsp_langpod.data.server;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 */

@AutoValue
public abstract class EntireChannelJson {
    public static final long DEFAULT_ID = 0;

    public static EntireChannelJson create(long id, int order, String title, String desc, String image, String url) {
        return new AutoValue_EntireChannelJson(id, order, title, desc, image, url);
    }

    public abstract long id();

    public abstract int order();

    public abstract String title();

    public abstract String desc();

    public abstract String image();

    public abstract String url();

    public static TypeAdapter<EntireChannelJson> typeAdapter(Gson gson) {
        return new AutoValue_EntireChannelJson.GsonTypeAdapter(gson);
    }
}
