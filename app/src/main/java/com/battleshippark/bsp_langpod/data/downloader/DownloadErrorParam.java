package com.battleshippark.bsp_langpod.data.downloader;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 */

@AutoValue
public abstract class DownloadErrorParam implements Parcelable {
    public abstract String identifier();

    public abstract Throwable throwable();


    public static DownloadErrorParam create(String identifier, Throwable throwable) {
        return new AutoValue_DownloadErrorParam(identifier, throwable);
    }
}
