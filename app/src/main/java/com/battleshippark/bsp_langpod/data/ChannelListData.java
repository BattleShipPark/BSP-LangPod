package com.battleshippark.bsp_langpod.data;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 */

@AutoValue
public abstract class ChannelListData {
    public static ChannelListData create(List<ChannelListItemData> items) {
        return new AutoValue_ChannelListData(items);
    }

    public abstract List<ChannelListItemData> items();

    public static TypeAdapter<ChannelListData> typeAdapter(Gson gson) {
        return new AutoValue_ChannelListData.GsonTypeAdapter(gson);
    }
}
