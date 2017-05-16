package com.battleshippark.bsp_langpod.data.db;

import java.util.Arrays;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 */

public class MyChannelRealm extends RealmObject {
    private long id;

    private int order;

    private String title;

    private String desc;

    private String copyright;

    private String image;

    private RealmList<EpisodeRealm> items;

    public MyChannelRealm() {
    }

    public MyChannelRealm(long id, int order, String title, String desc, String copyright,
                          String image, RealmList<EpisodeRealm> episodeRealmRealmList) {
        this.id = id;
        this.order = order;
        this.title = title;
        this.desc = desc;
        this.copyright = copyright;
        this.image = image;
        this.items = episodeRealmRealmList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
                ", order='" + order + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", copyright='" + copyright + '\'' +
                ", image='" + image + '\'' +
                ", items=" + Arrays.toString(items.toArray()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyChannelRealm that = (MyChannelRealm) o;

        if (id != that.id) return false;
        if (order != that.order) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        if (copyright != null ? !copyright.equals(that.copyright) : that.copyright != null)
            return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        return items != null ? items.equals(that.items) : that.items == null;

    }
}
