package com.battleshippark.bsp_langpod.data.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 */

public class DownloadRealm extends RealmObject {
    static final String FIELD_ID = "id";
    static final String FIELD_EPISODE_REALM = "episodeRealm";
    static final String FIELD_DOWNLOAD_DATE = "downloadDate";
    static final String FIELD_DOWNLOAD_STATE = "downloadState";

    @PrimaryKey
    private long id;
    private long channelId, episodeId;
    private Date downloadDate;
    private String downloadState;

    @Ignore
    private ChannelRealm channelRealm;
    @Ignore
    private EpisodeRealm episodeRealm;

    public DownloadRealm() {

    }

    DownloadRealm(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        this.channelRealm = channelRealm;
        this.episodeRealm = episodeRealm;
        this.channelId = channelRealm.getId();
        this.episodeId = episodeRealm.getId();
        this.downloadDate = new Date();
        this.downloadState = DownloadState.NOT_DOWNLOADED.name();
    }

    public long getId() {
        return id;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getEpisodeId() {
        return episodeId;
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

    public ChannelRealm getChannelRealm() {
        return channelRealm;
    }

    public void setChannelRealm(ChannelRealm channelRealm) {
        this.channelRealm = channelRealm;
    }

    public EpisodeRealm getEpisodeRealm() {
        return episodeRealm;
    }

    public void setEpisodeRealm(EpisodeRealm episodeRealm) {
        this.episodeRealm = episodeRealm;
    }


    public static DownloadRealm of(ChannelRealm channelRealm, EpisodeRealm episodeRealm) {
        return new DownloadRealm(channelRealm, episodeRealm);
    }

    @Override
    public String toString() {
        return "DownloadRealm{" +
                "id=" + id +
                ", channelRealm=" + channelRealm +
                ", episodeRealm=" + episodeRealm +
                ", downloadDate=" + downloadDate +
                ", downloadState=" + downloadState +
                '}';
    }

    public enum DownloadState {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED_DOWNLOAD
    }
}
