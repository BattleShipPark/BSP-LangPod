package com.battleshippark.bsp_langpod.data.db;

import java.util.Date;

import io.realm.RealmObject;

/**
 */

public class DownloadRealm extends RealmObject {
    public static final String FIELD_DOWNLOAD_DATE = "downloadDate";
    public static final String FIELD_DOWNLOAD_STATE = "downloadState";

    private EpisodeRealm episodeRealm;
    private Date downloadDate;
    private String downloadState;

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

    public enum DownloadState {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED;
    }
}
