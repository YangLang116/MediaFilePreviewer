package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.StartupUiUtil;
import com.twelvemonkeys.imageio.plugins.svg.MFSVGImageReader;
import com.twelvemonkeys.imageio.plugins.svg.SVGImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.svg.SVGReadParam;
import com.xtu.plugin.common.image.ImageCache;
import com.xtu.plugin.common.image.ImageThreadManager;
import com.xtu.plugin.common.ui.ScalePanel;
import com.xtu.plugin.common.utils.CloseUtils;
import com.xtu.plugin.common.utils.ImageUtils;
import com.xtu.plugin.common.utils.LogUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class SVGImageComponent extends ScalePanel {

    private static final int DEFAULT_SIZE = 100;

    private final long len;
    private final File file;
    private final OnLoadListener listener;
    private final ImageThreadManager threadManager;
    private final ImageCache<BufferedImage> imageCache;

    private int originalWidth;
    private int originalHeight;

    private volatile boolean hasLoadInfo;


    public SVGImageComponent(@NotNull VirtualFile vf, @NotNull OnLoadListener listener) {
        super();
        this.listener = listener;
        this.len = vf.getLength();
        this.file = new File(vf.getPath());
        this.imageCache = new ImageCache<>();
        this.threadManager = new ImageThreadManager("SVG-Renderer");
        this.scheduleLoad(true);
    }

    @Override
    protected void onScaleChanged() {
        this.scheduleLoad(false);
    }

    private void scheduleLoad(boolean initialLoad) {
        BufferedImage cachedImage = this.imageCache.get(scale);
        if (cachedImage != null) {
            ApplicationManager.getApplication().invokeLater(this::repaint);
            return;
        }
        this.threadManager.submit(() -> load(initialLoad));
    }

    @NotNull
    private SVGReadParam getReadParam(@NotNull MFSVGImageReader reader) {
        int targetSize = (int) (DEFAULT_SIZE * scale);
        float aspectRatio = (float) originalWidth / originalHeight;
        int displayWidth, displayHeight;
        if (originalWidth > originalHeight) {
            displayWidth = targetSize;
            displayHeight = (int) (targetSize / aspectRatio);
        } else {
            displayHeight = targetSize;
            displayWidth = (int) (targetSize * aspectRatio);
        }
        SVGReadParam param = reader.getDefaultReadParam();
        param.setSourceRenderSize(new Dimension(displayWidth, displayHeight));
        return param;
    }

    private void refreshCache(boolean initialLoad, @NotNull BufferedImage image) {
        // 更新缓存
        this.imageCache.put(scale, image);
        // 第一次加载时提供信息
        if (initialLoad || !hasLoadInfo) {
            listener.onGetInfo(originalWidth, originalHeight, image.getColorModel().getPixelSize(), len);
            hasLoadInfo = true;
        }
        this.repaint();
    }

    private void load(boolean initialLoad) {
        MFSVGImageReader reader = null;
        ImageInputStream stream = null;
        Application app = ApplicationManager.getApplication();
        try {
            // 创建Reader
            reader = new MFSVGImageReader(new SVGImageReaderSpi());
            stream = ImageIO.createImageInputStream(file);
            reader.setInput(stream);
            // 加载SVG信息
            if (initialLoad || !hasLoadInfo) {
                originalWidth = reader.getWidth(0);
                originalHeight = reader.getHeight(0);
            }
            // 加载图片
            ImageReadParam param = getReadParam(reader);
            final BufferedImage image = ImageUtils.loadImage(reader, 0, param);
            // 刷新UI
            if (image == null) {
                if (this.imageCache.isEmpty()) app.invokeLater(listener::onFail);
                return;
            }
            app.invokeLater(() -> refreshCache(initialLoad, image));
        } catch (Exception e) {
            LogUtils.error(e);
            if (this.imageCache.isEmpty()) app.invokeLater(listener::onFail);
        } finally {
            CloseUtils.close(stream);
            ImageUtils.dispose(reader);
        }
    }


    @Override
    public void paint(Graphics g) {
        g.setColor(JBColor.background());
        g.fillRect(0, 0, getWidth(), getHeight());
        BufferedImage imageToDraw = this.imageCache.find(scale);
        if (imageToDraw == null) return;
        // 设置高质量渲染
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 居中绘制
        Rectangle srcRect = new Rectangle(0, 0, imageToDraw.getWidth(), imageToDraw.getHeight());
        Rectangle dstRect = new Rectangle((getWidth() - imageToDraw.getWidth()) / 2, (getHeight() - imageToDraw.getHeight()) / 2, imageToDraw.getWidth(), imageToDraw.getHeight());
        StartupUiUtil.drawImage(g2d, imageToDraw, dstRect, srcRect, null);
    }

    public void dispose() {
        // 取消当前渲染任务
        this.threadManager.stopCurrentTask();
        // 清理所有缓存图像
        this.imageCache.clear();
    }

    public interface OnLoadListener {
        void onFail();

        void onGetInfo(int width, int height, int colorMode, long fileSize);
    }
}
