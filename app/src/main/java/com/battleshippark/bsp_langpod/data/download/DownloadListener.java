package com.battleshippark.bsp_langpod.data.download;

/**
 */

public interface DownloadListener {
    void update(long bytesRead, long contentLength, boolean done);
}
