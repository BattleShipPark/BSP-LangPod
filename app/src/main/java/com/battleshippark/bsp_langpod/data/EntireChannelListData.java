package com.battleshippark.bsp_langpod.data;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 */

@AutoValue
public abstract class EntireChannelListData {
    public static EntireChannelListData create(List<EntireChannelData> items) {
        return new AutoValue_EntireChannelListData(items);
    }

    public abstract List<EntireChannelData> items();

    public static TypeAdapter<EntireChannelListData> typeAdapter(Gson gson) {
        return new AutoValue_EntireChannelListData.GsonTypeAdapter(gson);
    }
}
