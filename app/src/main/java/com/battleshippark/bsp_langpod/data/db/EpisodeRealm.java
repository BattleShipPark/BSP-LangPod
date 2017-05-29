package com.battleshippark.bsp_langpod.data.db;

import java.util.Date;

import io.realm.RealmObject;

/**
 */

public class EpisodeRealm extends RealmObject {
    private String title;
    private String desc;
    private String url;
    private long length;
    private Date date;
    private String playState = PlayState.NOT_PLAYED.name();
    private String downloadState = DownloadState.NOT_DOWNLOADED.name();

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

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PlayState getPlayState() {
        return PlayState.valueOf(playState);
    }

    public void setPlayState(PlayState playState) {
        this.playState = playState.name();
    }

    public DownloadState getDownloadState() {
        return DownloadState.valueOf(downloadState);
    }

    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState.name();
    }

    @Override
    public String toString() {
        return "EpisodeRealm{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", url='" + url + '\'' +
                ", length=" + length +
                ", date=" + date +
                ", playState='" + playState + '\'' +
                ", downloadState='" + downloadState + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpisodeRealm that = (EpisodeRealm) o;

        if (length != that.length) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (playState != null ? !playState.equals(that.playState) : that.playState != null)
            return false;
        return downloadState != null ? downloadState.equals(that.downloadState) : that.downloadState == null;

    }

    private enum PlayState {
        NOT_PLAYED, PLAYING, PLAYED
    }

    private enum DownloadState {
        NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED
    }
}
