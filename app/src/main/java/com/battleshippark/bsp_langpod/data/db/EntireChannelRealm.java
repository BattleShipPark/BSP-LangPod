package com.battleshippark.bsp_langpod.data.db;

import io.realm.RealmObject;

/**
 */

public class EntireChannelRealm extends RealmObject {
    private int id;

    private String title;

    private String desc;

    private String image;

    public EntireChannelRealm() {
    }

    public EntireChannelRealm(int id, String title, String desc, String image) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        return "EntireChannelRealm{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
