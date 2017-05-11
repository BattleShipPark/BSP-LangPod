package com.battleshippark.bsp_langpod.data;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 */

@AutoValue
public abstract class MyChannelData {
    public static MyChannelData create(String title, String desc, String copyright, String image, List<EpisodeData> items) {
        return new AutoValue_MyChannelData(title, desc, copyright, image, items);
    }

    public abstract String title();

    public abstract String desc();

    public abstract String copyright();

    public abstract String image();

    public abstract List<EpisodeData> items();

    public static TypeAdapter<MyChannelData> typeAdapter(Gson gson) {
        return new AutoValue_MyChannelData.GsonTypeAdapter(gson);
    }
}
