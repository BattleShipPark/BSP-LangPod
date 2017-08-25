package com.battleshippark.bsp_langpod.data.downloader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 */

public class DownloadErrorParam implements Parcelable {
    private final long episodeId;
    private final Throwable throwable;

    public DownloadErrorParam(long episodeId, Throwable throwable) {
        this.episodeId = episodeId;
        this.throwable = throwable;
    }

    public long getEpisodeId() {
        return episodeId;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.episodeId);
        dest.writeSerializable(this.throwable);
    }

    protected DownloadErrorParam(Parcel in) {
        this.episodeId = in.readLong();
        this.throwable = (Throwable) in.readSerializable();
    }

    public static final Creator<DownloadErrorParam> CREATOR = new Creator<DownloadErrorParam>() {
        @Override
        public DownloadErrorParam createFromParcel(Parcel source) {
            return new DownloadErrorParam(source);
        }

        @Override
        public DownloadErrorParam[] newArray(int size) {
            return new DownloadErrorParam[size];
        }
    };
}
