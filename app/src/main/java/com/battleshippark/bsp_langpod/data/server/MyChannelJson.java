package com.battleshippark.bsp_langpod.data.server;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 */

@AutoValue
public abstract class MyChannelJson {
    public static MyChannelJson create(String title, String desc, String copyright, String image, List<EpisodeJson> items) {
        return new AutoValue_MyChannelJson(title, desc, copyright, image, items);
    }

    public abstract String title();

    public abstract String desc();

    public abstract String copyright();

    public abstract String image();

    public abstract List<EpisodeJson> episodes();

    public static TypeAdapter<MyChannelJson> typeAdapter(Gson gson) {
        return new AutoValue_MyChannelJson.GsonTypeAdapter(gson);
    }
}
