package com.battleshippark.bsp_langpod.service.downloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.battleshippark.bsp_langpod.data.downloader.DownloadCompleteParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadErrorParam;
import com.battleshippark.bsp_langpod.data.downloader.DownloadProgressParam;

import rx.functions.Action1;

/**
 */

public class DownloaderBroadcastReceiver extends BroadcastReceiver {
    private final Context context;
    private final Action1<DownloadProgressParam> progressAction;
    private final Action1<DownloadCompleteParam> completeAction;
    private final Action1<DownloadErrorParam> errorAction;
    private final IntentFilter intentFilter;
    private final ParamManager paramManger;

    public DownloaderBroadcastReceiver(Context context, Action1<DownloadProgressParam> progressAction,
                                       Action1<DownloadCompleteParam> completeAction,
                                       Action1<DownloadErrorParam> errorAction) {
        this.context = context;
        this.progressAction = progressAction;
        this.completeAction = completeAction;
        this.errorAction = errorAction;
        this.intentFilter = createIntentFilter();
        this.paramManger = new ParamManager();
    }

    public void register() {
        context.registerReceiver(this, intentFilter);
    }

    public void unregister() {
        context.unregisterReceiver(this);
    }

    public IntentFilter createIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloaderService.ACTION_PROGRESS);
        intentFilter.addAction(DownloaderService.ACTION_COMPLETED);
        intentFilter.addAction(DownloaderService.ACTION_ERROR);
        return intentFilter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (paramManger.hasProgressAction(intent)) {
            progressAction.call(paramManger.getProgressParam(intent));
        } else if (paramManger.hasCompleteAction(intent)) {
            completeAction.call(paramManger.getCompleteParam(intent));
        } else if (paramManger.hasErrorAction(intent)) {
            errorAction.call(paramManger.getErrorParam(intent));
        }
    }
}
