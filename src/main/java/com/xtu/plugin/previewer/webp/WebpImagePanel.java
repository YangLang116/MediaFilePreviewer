package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.Formats;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBBox;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.StartupUiUtil;
import com.xtu.plugin.common.ui.ScalePanel;
import com.xtu.plugin.common.utils.ImageUtils;
import com.xtu.plugin.common.utils.LogUtils;
import com.xtu.plugin.common.utils.PluginUtils;
import com.xtu.plugin.previewer.webp.entity.WebPDrawInfo;
import com.xtu.plugin.previewer.webp.entity.WebPFrame;
import com.xtu.plugin.previewer.webp.utils.WebpUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class WebpImagePanel extends ScalePanel {

    private volatile Dimension picDimension;

    private volatile VolatileImage image;
    private volatile Graphics2D imageGraphics;
    private volatile ImageReader imageReader;
    private volatile Rectangle drawRect;
    private final ExecutorService executor;
    private LinkedBlockingDeque<WebPDrawInfo> bufferedImageQueue;

    public WebpImagePanel(@NotNull VirtualFile webpFile) {
        super();
        this.setLayout(new BorderLayout());
        this.executor = Executors.newCachedThreadPool();
        this.executor.submit(() -> parseFile(webpFile));
    }

    private void parseFile(@NotNull VirtualFile webpFile) {
        imageReader = ImageUtils.getTwelveMonkeysRead(webpFile);
        if (imageReader == null) {
            LogUtils.info("WebpImagePanel imageReader == null");
            this.showErrorLabel();
            return;
        }
        final int imageNum = ImageUtils.getImageNum(imageReader);
        if (imageNum <= 0) {
            LogUtils.info("WebpImagePanel imageNum: " + imageNum);
            this.showErrorLabel();
            return;
        }
        BufferedImage firstFrame = ImageUtils.loadImage(imageReader, 0);
        if (firstFrame == null) {
            LogUtils.info("WebpImagePanel bufferedImage == null");
            this.showErrorLabel();
            return;
        }
        if (imageNum == 1) {
            parseStaticImage(firstFrame, webpFile);
        } else {
            parseDynamicImage(imageReader, firstFrame, webpFile);
        }
    }

    private void parseStaticImage(@NotNull BufferedImage firstFrame,
                                  @NotNull VirtualFile webpFile) {
        this.picDimension = new Dimension(firstFrame.getWidth(), firstFrame.getHeight());
        int colorMode = firstFrame.getColorModel().getPixelSize();
        showImageInfoLabel(this.picDimension, colorMode, 1, webpFile);
        initVolatileImage(this.picDimension);
        flushImageToVolatileGraphics(new Rectangle(picDimension), firstFrame);
    }

    private void parseDynamicImage(@NotNull ImageReader imageReader,
                                   @NotNull BufferedImage firstFrame,
                                   @NotNull VirtualFile webpFile) {
        List<WebPFrame> frameList = WebpUtils.loadAnimFrames(imageReader);
        if (frameList == null || frameList.isEmpty()) {
            this.showErrorLabel();
            return;
        }
        this.picDimension = WebpUtils.getPicSize(frameList);
        int colorMode = firstFrame.getColorModel().getPixelSize();
        showImageInfoLabel(this.picDimension, colorMode, frameList.size(), webpFile);
        Dimension contentSize = WebpUtils.getContentSize(frameList);
        initVolatileImage(contentSize);
        WebPFrame firstFrameInfo = frameList.get(0);
        flushImageToVolatileGraphics(firstFrameInfo.bounds, firstFrame);

        if (!PluginUtils.isAutoPlay()) return;
        this.bufferedImageQueue = new LinkedBlockingDeque<>(3);
        this.executor.submit(() -> prepareImage(this.executor, imageReader, frameList, bufferedImageQueue)
        );
        this.executor.submit(() -> scheduleDraw(this.executor, bufferedImageQueue, firstFrameInfo.duration));
    }


    private void prepareImage(@NotNull ExecutorService executor,
                              @NotNull ImageReader imageReader,
                              @NotNull List<WebPFrame> frameList,
                              @NotNull LinkedBlockingDeque<WebPDrawInfo> bufferedImageQueue) {
        int index = 1;
        int frameNum = frameList.size();
        try {
            while (!executor.isShutdown()) {
                BufferedImage image = ImageUtils.loadImage(imageReader, index);
                WebPFrame frame = frameList.get(index);
                WebPDrawInfo drawInfo = new WebPDrawInfo(image, frame);
                bufferedImageQueue.put(drawInfo);
                index = (index + 1) % frameNum;
            }
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel prepareImage: " + e.getMessage());
        }
    }

    @SuppressWarnings("BusyWait")
    private void scheduleDraw(@NotNull ExecutorService executor,
                              @NotNull LinkedBlockingDeque<WebPDrawInfo> bufferedImageQueue,
                              int firstFrameDelay) {
        try {
            Thread.sleep(firstFrameDelay);
            while (!executor.isShutdown()) {
                WebPDrawInfo drawInfo = bufferedImageQueue.take();
                flushImageToVolatileGraphics(drawInfo.frame.bounds, drawInfo.image);
                Thread.sleep(drawInfo.frame.duration);
            }
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel scheduleDraw: " + e.getMessage());
        }
    }

    private void flushImageToVolatileGraphics(@NotNull Rectangle drawRect, @NotNull BufferedImage bufferedImage) {
        this.drawRect = drawRect;
        Rectangle rect = new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        StartupUiUtil.drawImage(imageGraphics, bufferedImage, rect, rect, null);
        bufferedImage.flush();
        repaint();
    }

    private void showImageInfoLabel(Dimension size,
                                    int colorMode,
                                    int imageNum,
                                    @NotNull VirtualFile webpFile) {
        String imageInfo = String.format("%dx%d WebP (%d-bit color%s) %s",
                size.width, size.height,
                colorMode,
                imageNum == 1 ? "" : String.format(" / %d frames", imageNum),
                Formats.formatFileSize(webpFile.getLength())
        );
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            JBBox container = JBBox.createHorizontalBox();
            container.setBorder(JBUI.Borders.empty(10));
            container.add(JBBox.createHorizontalGlue());
            container.add(new JLabel(imageInfo));
            add(container, BorderLayout.NORTH);
        });
    }

    private void showErrorLabel() {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            String errorText = "Fail to load WEBP Image";
            JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
            add(errorLabel, BorderLayout.CENTER);
        });
    }

    private void initVolatileImage(@NotNull Dimension size) {
        int width = size.width;
        int height = size.height;
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration config = defaultScreenDevice.getDefaultConfiguration();
        try {
            this.image = config.createCompatibleVolatileImage(width, height, new ImageCapabilities(true), Transparency.TRANSLUCENT);
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel initVolatileImage: " + e.getMessage());
            this.image = config.createCompatibleVolatileImage(width, height, Transparency.TRANSLUCENT);
        }
        this.image.validate(null);
        this.image.setAccelerationPriority(1F);
        this.imageGraphics = this.image.createGraphics();
        this.imageGraphics.setComposite(AlphaComposite.Src);
        // 关闭防抖动
        this.imageGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        // 性能优先
        this.imageGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        this.imageGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        this.imageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    }

    @Override
    protected void onScaleChanged() {
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image == null || drawRect == null) return;
        float startX = (getWidth() - this.picDimension.width * scale) / 2;
        float startY = (getHeight() - this.picDimension.height * scale) / 2;
        Rectangle srcRect = new Rectangle(0, 0, drawRect.width, drawRect.height);
        Rectangle dstRect = new Rectangle(
                (int) (startX + drawRect.x * scale),
                (int) (startY + drawRect.y * scale),
                (int) (drawRect.width * scale),
                (int) (drawRect.height * scale));
        StartupUiUtil.drawImage(g, image, dstRect, srcRect, null);
    }

    public void dispose() {
        if (!this.executor.isShutdown()) {
            this.executor.shutdown();
        }
        if (this.bufferedImageQueue != null) {
            this.bufferedImageQueue.clear();
        }
        if (this.imageReader != null) {
            ImageUtils.dispose(this.imageReader);
        }
        if (this.image != null) {
            this.image.flush();
            this.image = null;
        }
        this.imageGraphics = null;
    }
}
