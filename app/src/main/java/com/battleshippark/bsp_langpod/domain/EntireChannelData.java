package com.battleshippark.bsp_langpod.domain;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 */

@AutoValue
public abstract class EntireChannelData {
    public static final long DEFAULT_ID = 0;

    public static EntireChannelData create(int order, String title, String desc, String image) {
        return new AutoValue_EntireChannelData(DEFAULT_ID, order, title, desc, image);
    }

    public static EntireChannelData create(long id, int order, String title, String desc, String image) {
        return new AutoValue_EntireChannelData(id, order, title, desc, image);
    }

    public abstract long id();

    public abstract int order();

    public abstract String title();

    public abstract String desc();

    public abstract String image();

    public static TypeAdapter<EntireChannelData> typeAdapter(Gson gson) {
        return new AutoValue_EntireChannelData.GsonTypeAdapter(gson);
    }
}
