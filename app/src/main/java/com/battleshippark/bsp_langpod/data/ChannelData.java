package com.battleshippark.bsp_langpod.data;

import com.google.auto.value.AutoValue;

import java.util.List;

/**
 */

@AutoValue
public abstract class ChannelData {
    public static ChannelData create(String title, String desc, String copyright, String image, List<ChannelItemData> items) {
        return new AutoValue_ChannelData(title, desc, copyright, image, items);
    }

    public abstract String title();

    public abstract String desc();

    public abstract String copyright();

    public abstract String image();

    public abstract List<ChannelItemData> items();
}
