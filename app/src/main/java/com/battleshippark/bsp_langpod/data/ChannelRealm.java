package com.battleshippark.bsp_langpod.data;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 */

public class ChannelRealm extends RealmObject {
    private String title;

    private String desc;

    private String copyright;

    private String image;

    private RealmList<ChannelItemRealm> items;

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

    public RealmList<ChannelItemRealm> getItems() {
        return items;
    }

    public void setItems(RealmList<ChannelItemRealm> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ChannelRealm{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", copyright='" + copyright + '\'' +
                ", image='" + image + '\'' +
                ", items=" + items +
                '}';
    }
}
