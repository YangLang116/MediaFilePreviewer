package com.xtu.plugin.previewer.common.ui;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBBox;
import com.xtu.plugin.previewer.common.utils.ImageUtils;
import com.xtu.plugin.previewer.common.utils.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.TimerTask;

public class ImagePanel extends JPanel {

    private Timer timer;
    private final ImageReader imageReader;

    public ImagePanel(@NotNull String imageType, @NotNull VirtualFile originFile, @Nullable ImageReader imageReader) {
        setLayout(new BorderLayout());
        this.imageReader = imageReader;
        loadContent(imageType, originFile, imageReader);
    }

    private void loadContent(@NotNull String imageType, @NotNull VirtualFile originFile, @Nullable ImageReader imageReader) {
        if (imageReader == null) {
            String errorText = String.format("Fail to load %s Image", imageType);
            JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
            add(errorLabel, BorderLayout.CENTER);
        } else {
            JBBox container = JBBox.createHorizontalBox();
            container.add(JBBox.createHorizontalGlue());
            JLabel imageInfoLabel = new JLabel();
            container.add(imageInfoLabel);
            add(container, BorderLayout.NORTH);
            ImageComponent imageComponent = new ImageComponent();
            imageComponent.registerKeyBoardEvent();
            add(imageComponent, BorderLayout.CENTER);
            Application application = ApplicationManager.getApplication();
            application.executeOnPooledThread(() -> refreshImageDisplayAsync(imageInfoLabel, imageComponent, imageType, originFile, imageReader));
        }
    }

    private void refreshImageDisplayAsync(@NotNull JLabel labelComponent,
                                          @NotNull ImageComponent imageComponent,
                                          @NotNull String imageType,
                                          @NotNull VirtualFile originFile,
                                          @NotNull ImageReader imageReader) {
        BufferedImage firstFrameImage = ImageUtils.loadImage(this.imageReader, 0);
        if (firstFrameImage == null) return;
        //刷新首帧数据
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            String imageInfo = ImageUtils.getImageInfo(imageType, firstFrameImage, originFile);
            labelComponent.setText(imageInfo);
            imageComponent.setImage(firstFrameImage);
        });
        //刷新帧数
        try {
            int frameNum = imageReader.getNumImages(true);
            if (frameNum < 2) return;
            this.timer = new Timer("Schedule Image Frame");
            this.timer.schedule(new TimerTask() {

                int frameIndex = 0;

                @Override
                public void run() {
                    frameIndex = (frameIndex + 1) % frameNum;
                    BufferedImage frameImage = ImageUtils.loadImage(imageReader, frameIndex);
                    if (frameImage != null) application.invokeLater(() -> imageComponent.setImage(frameImage));
                }
            }, 0, 16);
        } catch (Throwable e) {
            LogUtils.info("ImagePanel refreshImageDisplayAsync: " + e.getMessage());
        }
    }

    public void dispose() {
        if (timer != null) timer.cancel();
        if (imageReader != null) ImageUtils.dispose(imageReader);
    }

    private static class ImageComponent extends JComponent {

        private static final float MAX_SIZE = 400.0f;

        private float curScale = 1.0f;
        private Dimension preferredSize;
        private SoftReference<BufferedImage> imageRef;

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

        private void setImage(@NotNull BufferedImage image) {
            this.imageRef = new SoftReference<>(image);
            if (preferredSize == null) {
                int width = image.getWidth();
                int height = image.getHeight();
                if (width > MAX_SIZE || height > MAX_SIZE) {
                    final float factor = MAX_SIZE / Math.max(width, height);
                    this.preferredSize = new Dimension((int) (width * factor), (int) (height * factor));
                } else {
                    this.preferredSize = new Dimension(width, height);
                }
            }
            repaint();
        }


        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            BufferedImage bufferedImage = this.imageRef.get();
            if (bufferedImage == null) return;
            final int imgWidth = (int) (preferredSize.width * curScale);
            final int imgHeight = (int) (preferredSize.height * curScale);
            final int width = getWidth();
            final int height = getHeight();
            g.drawImage(bufferedImage, (width - imgWidth) / 2, (height - imgHeight) / 2, imgWidth, imgHeight, this);
        }
    }

}
