package com.battleshippark.bsp_langpod.data.db;

import android.support.annotation.Nullable;

import java.util.Arrays;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 */

public class ChannelRealm extends RealmObject {
    public static final String FIELD_ORDER = "order";
    public static final String FIELD_SUBSCRIBED = "subscribed";
    private long id;

    private int order;

    private String title;

    private String desc;

    private String image;

    private boolean subscribed;

    private String url;

    private RealmList<EpisodeRealm> episodes = new RealmList<>();

    public ChannelRealm() {
    }

    public ChannelRealm(long id, int order, String title, String desc, String image, String url, boolean subscribed) {
        this.id = id;
        this.order = order;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.url = url;
        this.subscribed = subscribed;
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

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Nullable
    public RealmList<EpisodeRealm> getEpisodes() {
        return episodes;
    }

    @Override
    public String toString() {
        return "ChannelRealm{" +
                "id=" + id +
                ", order=" + order +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", image='" + image + '\'' +
                ", subscribed=" + subscribed +
                ", url=" + url +
                ", episodes=" + ((episodes == null) ? "Null" : Arrays.toString(episodes.toArray())) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelRealm that = (ChannelRealm) o;

        if (id != that.id) return false;
        if (order != that.order) return false;
        if (subscribed != that.subscribed) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return episodes != null ? episodes.equals(that.episodes) : that.episodes == null;
    }
}
