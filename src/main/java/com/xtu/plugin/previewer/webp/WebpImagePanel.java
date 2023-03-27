package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBBox;
import com.intellij.util.ui.StartupUiUtil;
import com.xtu.plugin.common.utils.ImageUtils;
import com.xtu.plugin.common.utils.LogUtils;
import com.xtu.plugin.configuration.SettingsConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class WebpImagePanel extends JPanel {

    private final VirtualFile file;
    private final String fileExtension;
    private final ExecutorService threadPoolExecutor;
    private LinkedBlockingDeque<WebPDrawInfo> bufferedImageQueue;

    private Dimension picSize;
    private float curScale = 1.0f;
    private ImageReader imageReader;

    private volatile Rectangle bounds;
    private volatile VolatileImage image;
    private volatile Graphics2D imageGraphics;

    public WebpImagePanel(@NotNull VirtualFile file, @NotNull String fileExtension) {
        this.setLayout(new BorderLayout());
        this.registerKeyBoardEvent();
        this.file = file;
        this.fileExtension = fileExtension;
        this.threadPoolExecutor = Executors.newCachedThreadPool();
        this.threadPoolExecutor.submit(this::initFirstFrameInThreadPool);
    }


    private void initFirstFrameInThreadPool() {
        imageReader = ImageUtils.getTwelveMonkeysRead(fileExtension, file);
        if (imageReader == null) {
            LogUtils.info("WebpImagePanel imageReader == null");
            this.showErrorLabel();
            return;
        }
        final int imageNum = getImageNum(imageReader);
        if (imageNum <= 0) {
            LogUtils.info("WebpImagePanel imageNum: " + imageNum);
            this.showErrorLabel();
            return;
        }
        ArrayList<WebPFrame> frameList = getFrameList(imageReader);
        if (frameList == null || frameList.size() != imageNum) {
            LogUtils.info("WebpImagePanel imageNum: " + imageNum);
            this.showErrorLabel();
            return;
        }
        BufferedImage bufferedImage = ImageUtils.loadImage(imageReader, 0);
        if (bufferedImage == null) {
            LogUtils.info("WebpImagePanel bufferedImage == null");
            this.showErrorLabel();
            return;
        }
        showImageInfoLabel(bufferedImage, frameList);
        initVolatileImage(frameList);
        WebPFrame firstFrame = frameList.get(0);
        flushImageToVolatileGraphics(firstFrame.bounds, bufferedImage);
        boolean autoPlay = SettingsConfiguration.isAutoPlay();
        if (imageNum == 1 || !autoPlay) return;
        int frameDuration = firstFrame.duration;
        final int duration = frameDuration <= 0 ? 50 : frameDuration;
        this.bufferedImageQueue = new LinkedBlockingDeque<>(3);
        this.threadPoolExecutor.submit(() -> processBufferImageInThreadPool(this.threadPoolExecutor, imageReader, imageNum, frameList, bufferedImageQueue));
        this.threadPoolExecutor.submit(() -> repaintByIntervalInThreadPool(this.threadPoolExecutor, duration));
    }

    private Dimension getWebPSize(@NotNull ArrayList<WebPFrame> frameList) {
        int width = 0;
        int height = 0;
        for (WebPFrame frame : frameList) {
            Rectangle bounds = frame.bounds;
            width = Math.max(width, bounds.width + bounds.x);
            height = Math.max(height, bounds.height + bounds.y);
        }
        return new Dimension(width, height);
    }

    private Dimension getWebGraphicsSize(@NotNull ArrayList<WebPFrame> frameList) {
        int imageWidth = 0;
        int imageHeight = 0;
        for (WebPFrame frame : frameList) {
            Rectangle bounds = frame.bounds;
            imageWidth = Math.max(imageWidth, bounds.width);
            imageHeight = Math.max(imageHeight, bounds.height);
        }
        return new Dimension(imageWidth, imageHeight);
    }

    @SuppressWarnings({"unchecked", "SpellCheckingInspection"})
    private ArrayList<WebPFrame> getFrameList(@NotNull ImageReader imageReader) {
        try {
            Class<?> webPImageReader = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageReader");
            Field framesField = webPImageReader.getDeclaredField("frames");
            framesField.setAccessible(true);
            ArrayList<Object> frameList = (ArrayList<Object>) framesField.get(imageReader);
            Class<?> frameClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.AnimationFrame");
            Field boundsField = frameClass.getDeclaredField("bounds");
            boundsField.setAccessible(true);
            Field durationField = frameClass.getDeclaredField("duration");
            durationField.setAccessible(true);
            ArrayList<WebPFrame> result = new ArrayList<>();
            for (Object o : frameList) {
                Rectangle rect = (Rectangle) boundsField.get(o);
                int duration = (int) durationField.get(o);
                result.add(new WebPFrame(rect, duration));
            }
            return result;
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel fillFrameList: " + e.getMessage());
            return null;
        }
    }

    private int getImageNum(@NotNull ImageReader imageReader) {
        try {
            return imageReader.getNumImages(true);
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel initFirstFrameInThreadPool: " + e.getMessage());
            return 1;
        }
    }

    private void processBufferImageInThreadPool(@NotNull ExecutorService threadPoolExecutor, @NotNull ImageReader imageReader, int imageNum, @NotNull ArrayList<WebPFrame> frameList, @NotNull LinkedBlockingDeque<WebPDrawInfo> bufferedImageQueue) {
        int index = 1;
        try {
            while (!threadPoolExecutor.isShutdown()) {
                BufferedImage bufferedImage = ImageUtils.loadImage(imageReader, index);
                WebPFrame frame = frameList.get(index);
                WebPDrawInfo drawInfo = new WebPDrawInfo(bufferedImage, frame.bounds);
                bufferedImageQueue.put(drawInfo);
                index = (index + 1) % imageNum;
            }
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel processBufferImageInThreadPool: " + e.getMessage());
        }
    }

    @SuppressWarnings("BusyWait")
    private void repaintByIntervalInThreadPool(@NotNull ExecutorService threadPoolExecutor, int sleepTime) {
        try {
            while (!threadPoolExecutor.isShutdown()) {
                Thread.sleep(sleepTime);
                WebPDrawInfo drawInfo = bufferedImageQueue.take();
                flushImageToVolatileGraphics(drawInfo.bounds, drawInfo.image);
            }
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel repaintByIntervalInThreadPool: " + e.getMessage());
        }
    }

    private void flushImageToVolatileGraphics(@NotNull Rectangle bounds, @NotNull BufferedImage bufferedImage) {
        this.bounds = bounds;
        Rectangle rect = new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        StartupUiUtil.drawImage(imageGraphics, bufferedImage, rect, rect, null);
        bufferedImage.flush();
        repaint();
    }


    private void showImageInfoLabel(@NotNull BufferedImage bufferedImage, @NotNull ArrayList<WebPFrame> frameList) {
        this.picSize = getWebPSize(frameList);
        final ColorModel colorModel = bufferedImage.getColorModel();
        final int imageMode = colorModel.getPixelSize();
        String imageSize = StringUtil.formatFileSize(file.getLength());
        String imageInfo = String.format("%dx%d %s (%d-bit color) %s",
                bufferedImage.getWidth(), bufferedImage.getHeight(),
                fileExtension, imageMode, imageSize);
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            JBBox container = JBBox.createHorizontalBox();
            container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            container.add(JBBox.createHorizontalGlue());
            JLabel imageInfoLabel = new JLabel();
            imageInfoLabel.setText(imageInfo);
            container.add(imageInfoLabel);
            add(container, BorderLayout.NORTH);
        });
    }

    private void showErrorLabel() {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            String errorText = String.format("Fail to load %s Image", fileExtension);
            JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
            add(errorLabel, BorderLayout.CENTER);
        });
    }

    private void initVolatileImage(@NotNull ArrayList<WebPFrame> frameList) {
        Dimension graphicsSize = getWebGraphicsSize(frameList);
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration config = defaultScreenDevice.getDefaultConfiguration();
        try {
            this.image = config.createCompatibleVolatileImage(graphicsSize.width, graphicsSize.height, new ImageCapabilities(true), Transparency.TRANSLUCENT);
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel initVolatileImage: " + e.getMessage());
            this.image = config.createCompatibleVolatileImage(graphicsSize.width, graphicsSize.height, Transparency.TRANSLUCENT);
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

    public void registerKeyBoardEvent() {
        int maskKey = InputEvent.SHIFT_DOWN_MASK;
        registerKeyboardAction(e -> {
            this.curScale = curScale + 0.1f;  //放大
            repaint();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_W, maskKey), JComponent.WHEN_IN_FOCUSED_WINDOW);

        registerKeyboardAction(e -> {
            this.curScale = curScale - 0.1f;//缩小
            repaint();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, maskKey), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image == null || bounds == null) return;
        int x = (getWidth() - this.picSize.width) / 2;
        int y = (getHeight() - this.picSize.height) / 2;
        int imageWidth = bounds.width;
        int imageHeight = bounds.height;
        Rectangle srcRect = new Rectangle(0, 0, imageWidth, imageHeight);
        Rectangle dstRect = new Rectangle(
                (int) (x + bounds.x * curScale),
                (int) (y + bounds.y * curScale),
                (int) (imageWidth * curScale),
                (int) (imageHeight * curScale));
        StartupUiUtil.drawImage(g, image, dstRect, srcRect, null);
    }

    public void dispose() {
        if (!this.threadPoolExecutor.isShutdown()) {
            this.threadPoolExecutor.shutdown();
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

    private static class WebPDrawInfo {
        private final BufferedImage image;
        private final Rectangle bounds;

        public WebPDrawInfo(BufferedImage image, Rectangle bounds) {
            this.image = image;
            this.bounds = bounds;
        }
    }
}
