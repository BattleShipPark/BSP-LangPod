package com.battleshippark.bsp_langpod.data;

import java.util.List;

/**
 */

public class ChannelData {
    public String title;
    public String desc;
    public String copyright;
    public List<ChannelItemData> items;

    @Override
    public String toString() {
        return "ChannelData{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", copyright='" + copyright + '\'' +
                ", items=" + items +
                '}';
    }
}
