package com.battleshippark.bsp_langpod.service.downloader;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 */
public class DownloaderQueueTest {
    @Test
    public void 빈상태에서멈춰있다가_데이터들어오면가져오는지() throws InterruptedException {
        DownloaderQueue<Integer> queue = DownloaderQueue.getInstance();

        Integer[] results = new Integer[1];
        CountDownLatch latch = new CountDownLatch(1);

        Thread pollThread = Executors.defaultThreadFactory().newThread(() -> {
            try {
                results[0] = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread offerThread = Executors.defaultThreadFactory().newThread(() -> {
            queue.offer(0x1234);
            latch.countDown();
        });

        pollThread.start();
        Thread.sleep(100);
        assertThat(results[0]).isNull();

        offerThread.start();
        latch.await(100, TimeUnit.MILLISECONDS);
        assertThat(results[0]).isEqualTo(0x1234);
    }
}