package com.xtu.plugin.previewer.webp;

import com.twelvemonkeys.imageio.plugins.webp.MFAnimationFrame;
import com.xtu.plugin.common.utils.ImageUtils;
import com.xtu.plugin.common.utils.LogUtils;
import com.xtu.plugin.previewer.webp.entity.WebPDrawFrame;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class WebpAnimationController {

    private static final int FRAME_QUEUE_CAPACITY = 5;

    private final ImageReader imageReader;
    private final List<MFAnimationFrame> frameList;
    private final Consumer<WebPDrawFrame> onFrameUpdate;
    private final AtomicBoolean isAnimating = new AtomicBoolean(false);
    private final ArrayBlockingQueue<WebPDrawFrame> frameQueue;

    private ExecutorService frameLoader;
    private ScheduledExecutorService frameScheduler;

    public WebpAnimationController(@NotNull ImageReader imageReader, @NotNull List<MFAnimationFrame> frameList, @NotNull Consumer<WebPDrawFrame> frameUpdateCallback) {
        this.imageReader = imageReader;
        this.frameList = frameList;
        this.onFrameUpdate = frameUpdateCallback;
        this.frameQueue = new ArrayBlockingQueue<>(FRAME_QUEUE_CAPACITY);
    }

    private void initThreadPools() {
        frameLoader = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "WebP-Frame-Loader");
            t.setPriority(Thread.NORM_PRIORITY - 1);
            return t;
        });
        frameScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "WebP-Frame-Scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    private static void shutdownThreadPool(ExecutorService service) {
        // 先尝试正常关闭
        if (service != null && !service.isShutdown()) {
            service.shutdown();
            try {
                // 给线程一定的时间来完成当前任务
                if (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                service.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void clearFrameQueue() {
        WebPDrawFrame frame;
        while ((frame = frameQueue.poll()) != null) {
            if (frame.image != null) {
                frame.image.flush();
            }
        }
    }

    public void start() {
        if (frameList.isEmpty() || isAnimating.get()) return;
        isAnimating.set(true);
        initThreadPools();
        frameLoader.submit(this::prepareFrames);
        startFrameScheduler(frameList.get(0).duration);
    }

    public void stop() {
        if (!isAnimating.get()) return;
        isAnimating.set(false);
        shutdownThreadPool(frameLoader);
        shutdownThreadPool(frameScheduler);
        clearFrameQueue();
    }

    private void prepareFrames() {
        int index = 1;
        int frameNum = frameList.size();
        try {
            while (isAnimating.get() && !Thread.currentThread().isInterrupted()) {
                BufferedImage image = ImageUtils.loadImage(imageReader, index);
                if (image != null) {
                    MFAnimationFrame frame = frameList.get(index);
                    WebPDrawFrame drawInfo = new WebPDrawFrame(image, frame);
                    frameQueue.put(drawInfo);
                }
                index = (index + 1) % frameNum;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 线程被中断，正常退出
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    private void startFrameScheduler(int firstFrameDelay) {
        try {
            frameScheduler.schedule(this::scheduleFrames, firstFrameDelay, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    private void scheduleFrames() {
        try {
            if (isAnimating.get() && !Thread.currentThread().isInterrupted()) {
                WebPDrawFrame drawInfo = frameQueue.take();
                // 通知UI更新帧
                onFrameUpdate.accept(drawInfo);
                // 等待当前帧的显示时间
                frameScheduler.schedule(this::scheduleFrames, drawInfo.frame.duration, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }
} 