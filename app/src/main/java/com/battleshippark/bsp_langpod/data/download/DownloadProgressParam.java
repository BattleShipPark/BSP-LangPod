package com.battleshippark.bsp_langpod.data.download;

/**
 */

public class DownloadProgressParam {
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
}
