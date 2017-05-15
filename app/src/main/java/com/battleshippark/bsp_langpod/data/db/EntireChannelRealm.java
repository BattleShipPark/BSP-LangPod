package com.battleshippark.bsp_langpod.data.db;

import io.realm.RealmObject;

/**
 */

public class EntireChannelRealm extends RealmObject {
    private long id;

    private int order;

    private String title;

    private String desc;

    private String image;

    public EntireChannelRealm() {
    }

    public EntireChannelRealm(long id, int order, String title, String desc, String image) {
        this.id = id;
        this.order = order;
        this.title = title;
        this.desc = desc;
        this.image = image;
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
                ", order='" + order + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntireChannelRealm that = (EntireChannelRealm) o;

        if (id != that.id) return false;
        if (order != that.order) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        return image != null ? image.equals(that.image) : that.image == null;

    }
}
