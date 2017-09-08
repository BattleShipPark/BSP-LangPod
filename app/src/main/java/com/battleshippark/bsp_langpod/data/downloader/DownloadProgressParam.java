package com.battleshippark.bsp_langpod.data.downloader;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 */

@AutoValue
public abstract class DownloadProgressParam implements Parcelable {
    public abstract String identifier();

    public abstract long bytesRead();

    public abstract long contentLength();

    public abstract boolean done();

    public static DownloadProgressParam create(String identifier, long bytesRead, long contentLength, boolean done) {
        return new AutoValue_DownloadProgressParam(identifier, bytesRead, contentLength, done);
    }
}
