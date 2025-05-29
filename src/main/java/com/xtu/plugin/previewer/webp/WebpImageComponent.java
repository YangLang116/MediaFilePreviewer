package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.StartupUiUtil;
import com.twelvemonkeys.imageio.plugins.webp.MFAnimationFrame;
import com.twelvemonkeys.imageio.plugins.webp.MFWebPImageReader;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import com.xtu.plugin.common.ui.ScalePanel;
import com.xtu.plugin.common.utils.ImageUtils;
import com.xtu.plugin.common.utils.LogUtils;
import com.xtu.plugin.common.utils.PluginUtils;
import com.xtu.plugin.previewer.webp.entity.WebPDrawFrame;
import com.xtu.plugin.previewer.webp.utils.WebpUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WebpImageComponent extends ScalePanel {

    private Rectangle drawRect;
    private Image currentImage;
    private volatile Dimension imgSize;
    private volatile MFWebPImageReader imageReader;
    private volatile WebpAnimationController animController;

    private final OnLoadListener listener;

    public WebpImageComponent(@NotNull VirtualFile webpFile, @NotNull OnLoadListener listener) {
        super();
        this.listener = listener;
        this.executeOnPooledThread(() -> parseFile(webpFile));
    }

    private void executeOnPooledThread(@NotNull Runnable runnable) {
        ApplicationManager.getApplication().executeOnPooledThread(runnable);
    }

    private void flush(@NotNull Image img, @NotNull Rectangle bounds) {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            this.currentImage = img;
            this.drawRect = bounds;
            this.repaint();
        });
    }

    @Nullable
    private MFWebPImageReader initReader(@NotNull VirtualFile webpFile) {
        MFWebPImageReader reader = null;
        try {
            File file = new File(webpFile.getPath());
            reader = new MFWebPImageReader(new WebPImageReaderSpi());
            reader.setInput(ImageIO.createImageInputStream(file));
            return reader;
        } catch (Exception e) {
            LogUtils.error(e);
            ImageUtils.dispose(reader);
            return null;
        }
    }

    private void parseFile(@NotNull VirtualFile webpFile) {
        this.imageReader = initReader(webpFile);
        if (this.imageReader == null) {
            LogUtils.info("WebpImagePanel this.imageReader == null");
            this.listener.onFail();
            return;
        }
        final int imageNum = ImageUtils.getImageNum(this.imageReader);
        if (imageNum <= 0) {
            ImageUtils.dispose(this.imageReader);
            LogUtils.info("WebpImagePanel imageNum: " + imageNum);
            this.listener.onFail();
            return;
        }
        BufferedImage firstFrame = ImageUtils.loadImage(this.imageReader, 0);
        if (firstFrame == null) {
            ImageUtils.dispose(this.imageReader);
            LogUtils.info("WebpImagePanel bufferedImage == null");
            this.listener.onFail();
            return;
        }
        if (imageNum == 1) {
            parseStaticImage(firstFrame, webpFile);
        } else {
            parseDynamicImage(firstFrame, webpFile);
        }
    }

    private void parseStaticImage(@NotNull BufferedImage firstFrame, @NotNull VirtualFile webpFile) {
        this.imgSize = new Dimension(firstFrame.getWidth(), firstFrame.getHeight());
        int colorMode = firstFrame.getColorModel().getPixelSize();
        this.listener.onSuccess(this.imgSize, colorMode, 1, webpFile.getLength());
        this.flush(firstFrame, new Rectangle(imgSize));
    }

    private void parseDynamicImage(@NotNull BufferedImage firstFrame, @NotNull VirtualFile webpFile) {
        List<MFAnimationFrame> frameList = new ArrayList<>(this.imageReader.getFrames());
        if (frameList.isEmpty()) {
            this.listener.onFail();
            return;
        }
        this.imgSize = WebpUtils.getImgSize(frameList);
        int colorMode = firstFrame.getColorModel().getPixelSize();
        this.listener.onSuccess(this.imgSize, colorMode, frameList.size(), webpFile.getLength());
        MFAnimationFrame firstFrameInfo = frameList.get(0);
        this.flush(firstFrame, firstFrameInfo.bounds);

        if (!PluginUtils.isAutoPlay()) return;
        animController = new WebpAnimationController(this.imageReader, frameList, this::onFrameUpdate);
        animController.start();
    }


    @Override
    protected void onScaleChanged() {
        this.repaint();
    }

    // 动画帧更新回调方法
    private void onFrameUpdate(@NotNull WebPDrawFrame frame) {
        flush(frame.image, frame.frame.bounds);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(JBColor.background());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (currentImage == null || drawRect == null) return;
        ImageUtils.updateGraphicsHint((Graphics2D) g, false);
        float startX = (getWidth() - this.imgSize.width * scale) / 2;
        float startY = (getHeight() - this.imgSize.height * scale) / 2;
        Rectangle srcRect = new Rectangle(0, 0, drawRect.width, drawRect.height);
        Rectangle dstRect = new Rectangle((int) (startX + drawRect.x * scale), (int) (startY + drawRect.y * scale), (int) (drawRect.width * scale), (int) (drawRect.height * scale));
        StartupUiUtil.drawImage(g, currentImage, dstRect, srcRect, null);
    }

    public void dispose() {
        // 停止动画
        if (animController != null) animController.stop();
        // 释放图片
        if (currentImage != null) currentImage.flush();
        // 清理读取器和流
        ImageUtils.dispose(this.imageReader);
    }

    public interface OnLoadListener {
        void onFail();

        void onSuccess(@NotNull Dimension size, int colorMode, int imageNum, long fileSize);
    }
}
