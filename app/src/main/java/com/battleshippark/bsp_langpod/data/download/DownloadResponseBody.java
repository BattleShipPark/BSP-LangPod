package com.battleshippark.bsp_langpod.data.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 */

class DownloadResponseBody extends ResponseBody {
    private final String identifier;
    private final ResponseBody responseBody;
    private final DownloadListener downloadListener;
    private BufferedSource bufferedSource;

    DownloadResponseBody(String identifier, ResponseBody responseBody, DownloadListener downloadListener) {
        this.identifier = identifier;
        this.responseBody = responseBody;
        this.downloadListener = downloadListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;

    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);

                if (bytesRead != -1) {
                    totalBytesRead += bytesRead;
                }

                if (downloadListener != null) {
                    downloadListener.update(identifier, totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                }

                return bytesRead;
            }
        };
    }
}
