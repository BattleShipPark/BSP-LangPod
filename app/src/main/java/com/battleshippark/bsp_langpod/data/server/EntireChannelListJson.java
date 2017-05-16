package com.battleshippark.bsp_langpod.data.server;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 */

@AutoValue
public abstract class EntireChannelListJson {
    public static EntireChannelListJson create(List<EntireChannelJson> items) {
        return new AutoValue_EntireChannelListJson(items);
    }

    public abstract List<EntireChannelJson> items();

    public static TypeAdapter<EntireChannelListJson> typeAdapter(Gson gson) {
        return new AutoValue_EntireChannelListJson.GsonTypeAdapter(gson);
    }
}
