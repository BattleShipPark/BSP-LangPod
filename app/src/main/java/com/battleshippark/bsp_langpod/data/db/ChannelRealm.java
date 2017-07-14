package com.battleshippark.bsp_langpod.data.db;

import java.util.Arrays;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 */

public class ChannelRealm extends RealmObject {
    public static final String FIELD_ID = "id";
    public static final String FIELD_ORDER = "order";
    public static final String FIELD_SUBSCRIBED = "subscribed";

    @PrimaryKey
    private long id;

    private int order;

    private String title;

    private String desc;

    private String image;

    private String url;

    private String copyright;

    private RealmList<EpisodeRealm> episodes = new RealmList<>();

    private boolean subscribed;

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

    public ChannelRealm(long id, int order, String title, String desc, String image, String url, String copyright,
                        RealmList<EpisodeRealm> episodes, boolean subscribed) {
        this.id = id;
        this.order = order;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.url = url;
        this.copyright = copyright;
        this.episodes = episodes;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public RealmList<EpisodeRealm> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(RealmList<EpisodeRealm> episodes) {
        this.episodes = episodes;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
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
                ", url='" + url + '\'' +
                ", copyright='" + copyright + '\'' +
                ", episodes=" + Arrays.toString(episodes.toArray()) +
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
        if (copyright != null ? !copyright.equals(that.copyright) : that.copyright != null)
            return false;
        return episodes != null ? episodes.equals(that.episodes) : that.episodes == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + order;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (subscribed ? 1 : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (copyright != null ? copyright.hashCode() : 0);
        result = 31 * result + (episodes != null ? episodes.hashCode() : 0);
        return result;
    }
}
