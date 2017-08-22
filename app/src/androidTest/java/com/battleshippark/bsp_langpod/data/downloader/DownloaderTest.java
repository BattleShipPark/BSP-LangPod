package com.battleshippark.bsp_langpod.data.downloader;

import android.util.Log;

import com.battleshippark.bsp_langpod.AppPhase;

import org.junit.Test;

import java.io.File;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 */
public class DownloaderTest {
    @Test
    public void test() throws InterruptedException {
        Downloader downloader = new Downloader(new AppPhase(true));
        String url = "http://open.live.bbc.co.uk/mediaselector/5/redir/version/2.0/mediaset/audio-nondrm-download-low/proto/http/vpid/p058jwmk.mp3";
        PublishSubject<DownloadProgressParam> downloadProgress = PublishSubject.create();

        TestSubscriber<File> testSubscriber = new TestSubscriber<>();
        downloader.download("1", url, new File(getTargetContext().getExternalFilesDir("1"), "download_test").getAbsolutePath(),
                downloadProgress).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();

        File outputFile = testSubscriber.getOnNextEvents().get(0);
        Log.w("test", String.format("%s, %d", outputFile.getAbsolutePath(), outputFile.length()));
    }
}