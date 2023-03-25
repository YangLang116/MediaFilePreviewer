package com.xtu.plugin.common.ui;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
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
import java.awt.image.VolatileImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class VolatileImagePanel extends JPanel {

    private final VirtualFile file;
    private final String fileExtension;
    private final ExecutorService threadPoolExecutor;
    private LinkedBlockingDeque<BufferedImage> bufferedImageQueue;

    private float curScale = 1.0f;
    private ImageReader imageReader;
    private VolatileImage image;
    private Graphics2D imageGraphics;

    public VolatileImagePanel(@NotNull VirtualFile file, @NotNull String fileExtension) {
        this.setLayout(new BorderLayout());
        this.file = file;
        this.fileExtension = fileExtension;
        this.threadPoolExecutor = Executors.newCachedThreadPool();
        this.registerKeyBoardEvent();
        threadPoolExecutor.submit(this::initFirstFrameInThreadPool);
    }

    private void initFirstFrameInThreadPool() {
        imageReader = ImageUtils.getTwelveMonkeysRead(fileExtension, file);
        if (imageReader == null) {
            LogUtils.info("VolatileImagePanel imageReader == null");
            this.showErrorLabel();
            return;
        }
        BufferedImage bufferedImage = ImageUtils.loadImage(imageReader, 0);
        if (bufferedImage == null) {
            LogUtils.info("VolatileImagePanel bufferedImage == null");
            this.showErrorLabel();
            return;
        }
        showImageInfoLabel(bufferedImage);
        initVolatileImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        flushImageToVolatileGraphics(bufferedImage);
        boolean autoPlay = SettingsConfiguration.isAutoPlay();
        if (!autoPlay) return;
        final int imageNum = getImageNum(imageReader);
        if (imageNum <= 1) return;
        this.bufferedImageQueue = new LinkedBlockingDeque<>(5);
        this.threadPoolExecutor.submit(() -> processBufferImageInThreadPool(this.threadPoolExecutor, imageReader, imageNum, bufferedImageQueue));
        this.threadPoolExecutor.submit(() -> repaintByIntervalInThreadPool(this.threadPoolExecutor));
    }

    private int getImageNum(@NotNull ImageReader imageReader) {
        try {
            return imageReader.getNumImages(true);
        } catch (Exception e) {
            LogUtils.info("VolatileImagePanel initFirstFrameInThreadPool: " + e.getMessage());
            return 1;
        }
    }

    private void processBufferImageInThreadPool(@NotNull ExecutorService threadPoolExecutor, @NotNull ImageReader imageReader, int imageNum, @NotNull LinkedBlockingDeque<BufferedImage> bufferedImageQueue) {
        int index = 1;
        try {
            while (!threadPoolExecutor.isShutdown()) {
                BufferedImage bufferedImage = ImageUtils.loadImage(imageReader, index);
                bufferedImageQueue.put(bufferedImage);
                index = (index + 1) % imageNum;
            }
        } catch (Exception e) {
            LogUtils.info("VolatileImagePanel processBufferImageInThreadPool: " + e.getMessage());
        }
    }

    private void repaintByIntervalInThreadPool(@NotNull ExecutorService threadPoolExecutor) {
        try {
            while (!threadPoolExecutor.isShutdown()) {
                Thread.sleep(50);
                BufferedImage bufferedImage = bufferedImageQueue.take();
                flushImageToVolatileGraphics(bufferedImage);
            }
        } catch (Exception e) {
            LogUtils.info("VolatileImagePanel repaintByIntervalInThreadPool: " + e.getMessage());
        }
    }

    private void flushImageToVolatileGraphics(@NotNull BufferedImage bufferedImage) {
        Rectangle rect = new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        StartupUiUtil.drawImage(imageGraphics, bufferedImage, rect, rect, null);
        bufferedImage.flush();
        repaint();
    }

    private void showImageInfoLabel(@NotNull BufferedImage bufferedImage) {
        String ImageInfo = ImageUtils.getImageInfo(fileExtension, bufferedImage, file);
        JBBox container = JBBox.createHorizontalBox();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        container.add(JBBox.createHorizontalGlue());
        JLabel imageInfoLabel = new JLabel();
        imageInfoLabel.setText(ImageInfo);
        container.add(imageInfoLabel);
        add(container, BorderLayout.NORTH);
    }

    private void showErrorLabel() {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            String errorText = String.format("Fail to load %s Image", fileExtension);
            JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
            add(errorLabel, BorderLayout.CENTER);
        });
    }

    private void initVolatileImage(int width, int height) {
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration config = defaultScreenDevice.getDefaultConfiguration();
        try {
            this.image = config.createCompatibleVolatileImage(width, height, new ImageCapabilities(true), Transparency.TRANSLUCENT);
        } catch (Exception e) {
            LogUtils.info("VolatileImagePanel initVolatileImage: " + e.getMessage());
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
        if (image == null) return;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        int displayWidth = (int) (imageWidth * curScale);
        int displayHeight = (int) (imageHeight * curScale);
        int displayX = (getWidth() - displayWidth) / 2;
        int displayY = (getHeight() - displayHeight) / 2;
        Rectangle srcRect = new Rectangle(0, 0, imageWidth, imageHeight);
        Rectangle dstRect = new Rectangle(displayX, displayY, displayWidth, displayHeight);
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
}
