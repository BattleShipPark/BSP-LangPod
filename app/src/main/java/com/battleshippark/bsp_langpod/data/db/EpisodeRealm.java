package com.battleshippark.bsp_langpod.data.db;

import com.battleshippark.bsp_langpod.R;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 */

public class EpisodeRealm extends RealmObject {
    static final String FIELD_ID = "id";

    @PrimaryKey
    private long id;
    private String title;
    private String desc;
    private String url;
    private long length;
    private Date date;
    private String playState = PlayState.NOT_PLAYED.name();
    private int playTime;
    private String downloadState = DownloadState.NOT_DOWNLOADED.name();
    private String downloadedPath;

    @Ignore
    private long downloadedBytes, totalBytes;

    public EpisodeRealm() {
    }

    public EpisodeRealm(long id, String title, String desc, String url, Date date) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.date = date;
    }

    public EpisodeRealm(EpisodeRealm realm) {
        this(realm.id, realm.title, realm.desc, realm.url, realm.date);
        this.length = realm.length;
        this.playState = realm.playState;
        this.playTime = realm.playTime;
        this.downloadState = realm.downloadState;
        this.downloadedPath = realm.downloadedPath;
        this.downloadedBytes = realm.downloadedBytes;
        this.totalBytes = realm.totalBytes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        return new Date(date.getTime());
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

    public int getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(int timeInMs) {
        this.playTime = timeInMs;
    }

    public DownloadState getDownloadState() {
        return DownloadState.valueOf(downloadState);
    }

    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState.name();
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public String getDownloadedPath() {
        return downloadedPath;
    }

    public void setDownloadedPath(String downloadedPath) {
        this.downloadedPath = downloadedPath;
    }

    @Override
    public String toString() {
        return "EpisodeRealm{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", url='" + url + '\'' +
                ", length=" + length +
                ", date=" + date +
                ", playState='" + playState + '\'' +
                ", playTime='" + playTime + '\'' +
                ", downloadState='" + downloadState + '\'' +
                ", downloadedPath='" + downloadedPath + '\'' +
                ", downloadedBytes=" + downloadedBytes +
                ", totalBytes=" + totalBytes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EpisodeRealm that = (EpisodeRealm) o;

        if (id != that.id) return false;
        if (length != that.length) return false;
        if (downloadedBytes != that.downloadedBytes) return false;
        if (totalBytes != that.totalBytes) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (playState != null ? !playState.equals(that.playState) : that.playState != null)
            return false;
        if (playTime != that.playTime) return false;
        if (downloadState != null ? !downloadState.equals(that.downloadState) : that.downloadState != null)
            return false;
        return downloadedPath != null ? downloadedPath.equals(that.downloadedPath) : that.downloadedPath == null;

    }

    public enum PlayState {
        NOT_PLAYED, PLAYING, PAUSE, PLAYED
    }

    public enum DownloadState {
        NOT_DOWNLOADED(R.string.episode_not_downloaded),
        DOWNLOADING(R.string.episode_downloading),
        DOWNLOADED(R.string.episode_downloaded),
        FAILED_DOWNLOAD(R.string.episode_failed_download);

        public int resId;

        DownloadState(int resId) {
            this.resId = resId;
        }
    }
}
