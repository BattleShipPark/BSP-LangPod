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

    public static MyChannelRealm create(long id, int order, String title, String desc, String copyright, String image) {
        MyChannelRealm myChannelRealm = new MyChannelRealm();
        myChannelRealm.setId(id);
        myChannelRealm.setOrder(order);
        myChannelRealm.setTitle(title);
        myChannelRealm.setDesc(desc);
        myChannelRealm.setCopyright(copyright);
        myChannelRealm.setImage(image);
        return myChannelRealm;
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
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", copyright='" + copyright + '\'' +
                ", image='" + image + '\'' +
                ", items=" + Arrays.toString(items.toArray()) +
                '}';
    }
}
