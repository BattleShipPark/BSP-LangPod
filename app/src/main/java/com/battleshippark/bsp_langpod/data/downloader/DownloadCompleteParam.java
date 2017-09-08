package com.battleshippark.bsp_langpod.data.downloader;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.io.File;

/**
 */

@AutoValue
public abstract class DownloadCompleteParam implements Parcelable {
    public abstract String identifier();

    public abstract File file();

    public static DownloadCompleteParam create(String identifier, File file) {
        return new AutoValue_DownloadCompleteParam(identifier, file);
    }
}
