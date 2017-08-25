package com.battleshippark.bsp_langpod.data.downloader;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 */

public class DownloadCompleteParam implements Parcelable {
    private final long episodeId;
    private final File file;

    public DownloadCompleteParam(long episodeId, File file) {
        this.episodeId = episodeId;
        this.file = file;
    }

    public long getEpisodeId() {
        return episodeId;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.episodeId);
        dest.writeSerializable(this.file);
    }

    protected DownloadCompleteParam(Parcel in) {
        this.episodeId = in.readLong();
        this.file = (File) in.readSerializable();
    }

    public static final Creator<DownloadCompleteParam> CREATOR = new Creator<DownloadCompleteParam>() {
        @Override
        public DownloadCompleteParam createFromParcel(Parcel source) {
            return new DownloadCompleteParam(source);
        }

        @Override
        public DownloadCompleteParam[] newArray(int size) {
            return new DownloadCompleteParam[size];
        }
    };
}
