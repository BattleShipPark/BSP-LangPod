package com.battleshippark.bsp_langpod.data.db;

import io.realm.RealmObject;

/**
 */

public class EpisodeRealm extends RealmObject {
    private String title;
    private String desc;
    private String url;

    public EpisodeRealm() {
    }

    public EpisodeRealm(String title, String desc, String url) {
        this.title = title;
        this.desc = desc;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "EpisodeRealm{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpisodeRealm that = (EpisodeRealm) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        return url != null ? url.equals(that.url) : that.url == null;

    }
}
