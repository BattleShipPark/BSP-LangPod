package com.battleshippark.bsp_langpod.data.download;

import android.util.Log;

import com.battleshippark.bsp_langpod.AppPhase;

import org.junit.Test;

import java.io.File;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 */
public class DownloaderTest {
    @Test
    public void test() throws InterruptedException {
        String url = "http://open.live.bbc.co.uk/mediaselector/5/redir/version/2.0/mediaset/audio-nondrm-download-low/proto/http/vpid/p058jwmk.mp3";
        Downloader downloader = new Downloader(Schedulers.immediate(), Schedulers.immediate(), new AppPhase(true),
                (bytesRead, contentLength, done) -> Log.w("test", String.format("%d, %d, %s", bytesRead, contentLength, done))
        );

        TestSubscriber<File> testSubscriber = new TestSubscriber<>();
        downloader.download(url, new File(getTargetContext().getExternalFilesDir(null), "download_test").getAbsolutePath()).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();

        File outputFile = testSubscriber.getOnNextEvents().get(0);
        Log.w("test", String.format("%s, %d", outputFile.getAbsolutePath(), outputFile.length()));
    }
}