package com.battleshippark.bsp_langpod.data.server;

/**
 */

public class EpisodeData {
    public String title;
    public String desc;
    public String url;

    @Override
    public String toString() {
        return "EpisodeData{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
