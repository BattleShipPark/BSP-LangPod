package com.battleshippark.bsp_langpod.domain;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 */

@AutoValue
public abstract class MyChannelData {
    public static final long EMPTY_ID = 0;
    public static final int EMPTY_ORDER = 0;

    public static MyChannelData create(String title, String desc, String copyright, String image, String url, List<EpisodeData> items) {
        return new AutoValue_MyChannelData(EMPTY_ID, EMPTY_ORDER, title, desc, copyright, image, url, items);
    }

    public static MyChannelData create(long id, int order, String title, String desc, String copyright, String image, String url, List<EpisodeData> items) {
        return new AutoValue_MyChannelData(id, order, title, desc, copyright, image, url, items);
    }

    public abstract long id();

    public abstract int order();

    public abstract String title();

    public abstract String desc();

    public abstract String copyright();

    public abstract String image();

    public abstract String url();

    @Nullable
    public abstract List<EpisodeData> items();

    public static TypeAdapter<MyChannelData> typeAdapter(Gson gson) {
        return new AutoValue_MyChannelData.GsonTypeAdapter(gson);
    }
}
