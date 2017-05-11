package com.battleshippark.bsp_langpod.data.server.rss;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

/**
 */

@GsonTypeAdapterFactory
public abstract class GsonAvTypeAdapterFactory implements TypeAdapterFactory {
    public static TypeAdapterFactory create() {
        return new AutoValueGson_GsonAvTypeAdapterFactory();
    }
}
