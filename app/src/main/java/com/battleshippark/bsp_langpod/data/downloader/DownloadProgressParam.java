package com.battleshippark.bsp_langpod.data.downloader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 */

public class DownloadProgressParam implements Parcelable {
    public final String identifier;
    public final long bytesRead;
    public final long contentLength;
    public final boolean done;

    public DownloadProgressParam(String identifier, long bytesRead, long contentLength, boolean done) {
        this.identifier = identifier;
        this.bytesRead = bytesRead;
        this.contentLength = contentLength;
        this.done = done;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeLong(this.bytesRead);
        dest.writeLong(this.contentLength);
        dest.writeByte(this.done ? (byte) 1 : (byte) 0);
    }

    protected DownloadProgressParam(Parcel in) {
        this.identifier = in.readString();
        this.bytesRead = in.readLong();
        this.contentLength = in.readLong();
        this.done = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DownloadProgressParam> CREATOR = new Parcelable.Creator<DownloadProgressParam>() {
        @Override
        public DownloadProgressParam createFromParcel(Parcel source) {
            return new DownloadProgressParam(source);
        }

        @Override
        public DownloadProgressParam[] newArray(int size) {
            return new DownloadProgressParam[size];
        }
    };
}
