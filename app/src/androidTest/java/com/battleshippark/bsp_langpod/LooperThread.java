package com.battleshippark.bsp_langpod;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 */

public class LooperThread {
    private final HandlerThread handlerThread;
    private final Handler handler;
    private final Executor executor;
    private CountDownLatch latch;

    public LooperThread(String name) {
        handlerThread = new HandlerThread(name);
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());
        executor = new TestExecutor(handler);
    }

    public Executor getExecutor() {
        return executor;
    }

    public void run(Runnable command) {
        latch = new CountDownLatch(1);
        handler.post(() -> {
            command.run();
            latch.countDown();
        });
    }

    public void await() throws InterruptedException {
        latch.await();
    }

    private class TestExecutor implements Executor {
        private final Handler handler;

        TestExecutor(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    }
}
