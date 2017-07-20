package com.battleshippark.bsp_langpod.data.download;

/**
 */

public interface DownloadListener {
    void update(String identifier, long bytesRead, long contentLength, boolean done);
}
