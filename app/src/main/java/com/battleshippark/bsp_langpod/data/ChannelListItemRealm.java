package com.battleshippark.bsp_langpod.data;

import io.realm.RealmObject;

/**
 */

public class ChannelListItemRealm extends RealmObject {
    private String title;

    private String desc;

    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ChannelListItemRealm{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
