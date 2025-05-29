package com.xtu.plugin.common.image;

import java.util.concurrent.*;

public final class ImageThreadManager {

    private Future<?> currentRenderTask;
    private final ExecutorService service;

    public ImageThreadManager(String threadName) {
        this.service = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10), r -> {
            Thread thread = new Thread(r, threadName);
            thread.setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        }, new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void submit(Runnable runnable) {
        if (currentRenderTask != null && !currentRenderTask.isDone()) {
            currentRenderTask.cancel(false);
        }
        currentRenderTask = service.submit(runnable);
    }

    public void stopCurrentTask() {
        if (currentRenderTask != null) {
            currentRenderTask.cancel(true);
            currentRenderTask = null;
        }
        if (!service.isShutdown()) {
            service.shutdown();
        }
    }
}
