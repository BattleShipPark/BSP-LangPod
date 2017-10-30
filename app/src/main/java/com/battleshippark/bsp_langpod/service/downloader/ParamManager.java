package com.battleshippark.bsp_langpod.service.downloader;

import android.content.Intent;

import com.battleshippark.bsp_langpod.data.db.EpisodeRealm;
import com.battleshippark.bsp_langpod.data.downloader.DownloadCompleteParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadErrorParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;

import java.io.File;

import static com.battleshippark.bsp_langpod.service.downloader.DownloaderService.ACTION_COMPLETED;
import static com.battleshippark.bsp_langpod.service.downloader.DownloaderService.ACTION_ERROR;
import static com.battleshippark.bsp_langpod.service.downloader.DownloaderService.ACTION_PROGRESS;

/**
 */

class ParamManager {
    private static final String KEY_PROGRESS = "keyProgress";
    private static final String KEY_COMPLETE = "keyComplete";
    private static final String KEY_ERROR = "keyError";

    boolean hasProgressAction(Intent intent) {
        return checkIntent(intent, DownloaderService.ACTION_PROGRESS);
    }

    boolean hasCompleteAction(Intent intent) {
        return checkIntent(intent, DownloaderService.ACTION_COMPLETED);
    }

    boolean hasErrorAction(Intent intent) {
        return checkIntent(intent, DownloaderService.ACTION_ERROR);
    }

    private boolean checkIntent(Intent intent, String action) {
        return intent != null && intent.getAction() != null
                && intent.getAction().equals(action);
    }

    Intent getProgressIntent(DownloadProgressParam param) {
        Intent intent = new Intent(ACTION_PROGRESS);
        intent.putExtra(KEY_PROGRESS, param);
        return intent;
    }

    Intent getCompleteIntent(EpisodeRealm episodeRealm, File file) {
        Intent intent = new Intent(ACTION_COMPLETED);
        intent.putExtra(KEY_COMPLETE, DownloadCompleteParam.create(String.valueOf(episodeRealm.getId()), file));
        return intent;
    }

    Intent getErrorIntent(EpisodeRealm episodeRealm, Throwable throwable) {
        Intent intent = new Intent(ACTION_ERROR);
        intent.putExtra(KEY_ERROR, DownloadErrorParam.create(String.valueOf(episodeRealm.getId()), throwable));
        return intent;
    }

    DownloadProgressParam getProgressParam(Intent intent) {
        return intent.getParcelableExtra(KEY_PROGRESS);
    }

    DownloadCompleteParam getCompleteParam(Intent intent) {
        return intent.getParcelableExtra(KEY_COMPLETE);
    }

    DownloadErrorParam getErrorParam(Intent intent) {
        return intent.getParcelableExtra(KEY_ERROR);
    }
}
