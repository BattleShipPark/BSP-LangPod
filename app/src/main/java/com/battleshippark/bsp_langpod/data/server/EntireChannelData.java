package com.battleshippark.bsp_langpod.data.server;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 */

@AutoValue
public abstract class EntireChannelData {
    public static final long DEFAULT_ID = 0;

    public static EntireChannelData create(String title, String desc, String image) {
        return new AutoValue_EntireChannelData(DEFAULT_ID, title, desc, image);
    }

    public static EntireChannelData create(long id, String title, String desc, String image) {
        return new AutoValue_EntireChannelData(id, title, desc, image);
    }

    public abstract long id();

    public abstract String title();

    public abstract String desc();

    public abstract String image();

    public static TypeAdapter<EntireChannelData> typeAdapter(Gson gson) {
        return new AutoValue_EntireChannelData.GsonTypeAdapter(gson);
    }
}
