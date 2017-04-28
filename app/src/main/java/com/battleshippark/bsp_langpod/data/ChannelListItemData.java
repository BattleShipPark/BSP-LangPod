package com.battleshippark.bsp_langpod.data;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 */

@AutoValue
public abstract class ChannelListItemData {
    public abstract String title();

    public abstract String desc();

    public abstract String image();

    public static TypeAdapter<ChannelListItemData> typeAdapter(Gson gson) {
        return new AutoValue_ChannelListItemData.GsonTypeAdapter(gson);
    }
}
