package com.battleshippark.bsp_langpod.data.db;

import java.util.Date;

import io.realm.RealmObject;

/**
 */

public class DownloadRealm extends RealmObject {
    static final String FIELD_EPISODE_REALM = "episodeRealm";
    static final String FIELD_DOWNLOAD_DATE = "downloadDate";
    static final String FIELD_DOWNLOAD_STATE = "downloadState";

    private EpisodeRealm episodeRealm;
    private Date downloadDate;
    private String downloadState;

    public DownloadRealm() {

    }

    DownloadRealm(EpisodeRealm episodeRealm) {
        this.episodeRealm = episodeRealm;
        this.downloadDate = new Date();
        this.downloadState = DownloadState.NOT_DOWNLOADED.name();
    }

    public EpisodeRealm getEpisodeRealm() {
        return episodeRealm;
    }

    public void setEpisodeRealm(EpisodeRealm episodeRealm) {
        this.episodeRealm = episodeRealm;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public DownloadState getDownloadState() {
        return DownloadState.valueOf(downloadState);
    }

    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState.name();
    }

    @Override
    public String toString() {
        return "DownloadRealm{" +
                "episodeRealm=" + episodeRealm +
                ", downloadDate=" + downloadDate +
                ", downloadState=" + downloadState +
                '}';
    }

    public static DownloadRealm of(EpisodeRealm episodeRealm) {
        return new DownloadRealm(episodeRealm);
    }

    public enum DownloadState {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED_DOWNLOAD
    }
}
