package com.battleshippark.bsp_langpod.data;

import java.util.Arrays;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 */

public class MyChannelRealm extends RealmObject {
    private int id;

    private String title;

    private String desc;

    private String copyright;

    private String image;

    private RealmList<EpisodeRealm> items;

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

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public RealmList<EpisodeRealm> getItems() {
        return items;
    }

    public void setItems(EpisodeRealm items) {
        this.items.clear();
        this.items.add(items);
    }

    @Override
    public String toString() {
        return "MyChannelRealm{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", copyright='" + copyright + '\'' +
                ", image='" + image + '\'' +
                ", items=" + Arrays.toString(items.toArray()) +
                '}';
    }
}
