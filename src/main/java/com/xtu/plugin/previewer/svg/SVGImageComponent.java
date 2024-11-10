package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.StartupUiUtil;
import com.twelvemonkeys.imageio.plugins.svg.MFSVGImageReader;
import com.twelvemonkeys.imageio.plugins.svg.SVGImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.svg.SVGReadParam;
import com.xtu.plugin.common.ui.ScalePanel;
import com.xtu.plugin.common.utils.CloseUtils;
import com.xtu.plugin.common.utils.ImageUtils;
import com.xtu.plugin.common.utils.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SVGImageComponent extends ScalePanel {

    private static final int sDefaultSize = 100;

    private final VirtualFile svgFile;
    private final OnLoadListener listener;
    private final ExecutorService executorService;

    private volatile boolean hasLoadInfo;
    private volatile BufferedImage image;

    public SVGImageComponent(@NotNull VirtualFile svgFile, @NotNull OnLoadListener listener) {
        super();
        this.svgFile = svgFile;
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor();
        this.render();
    }

    @Override
    protected void onScaleChanged() {
        this.render();
    }

    @Nullable
    private MFSVGImageReader initReader() {
        MFSVGImageReader reader = new MFSVGImageReader(new SVGImageReaderSpi());
        ImageInputStream stream = null;
        try {
            File file = new File(svgFile.getPath());
            stream = ImageIO.createImageInputStream(file);
            reader.setInput(stream, false, true);
            return reader;
        } catch (Exception e) {
            LogUtils.error(e);
            CloseUtils.close(stream);
            ImageUtils.dispose(reader);
            return null;
        }
    }

    private void render() {
        executorService.submit(() -> {
            MFSVGImageReader reader = initReader();
            if (reader == null) {
                image = null;
                listener.onFail();
                repaint();
                return;
            }
            try {
                SVGReadParam param = reader.getDefaultReadParam();
                int displaySize = (int) (sDefaultSize * scale);
                param.setSourceRenderSize(new Dimension(displaySize, displaySize));
                image = ImageUtils.loadImage(reader, 0, param);
                if (image == null) {
                    listener.onFail();
                } else if (!hasLoadInfo) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    listener.onGetInfo(width, height, image.getColorModel().getPixelSize(), svgFile.getLength());
                    hasLoadInfo = true;
                }
            } catch (Exception e) {
                image = null;
                listener.onFail();
            } finally {
                ImageUtils.dispose(reader);
            }
            repaint();
        });
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(JBColor.background());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (image == null) return;
        Rectangle srcRect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        Rectangle dstRect = new Rectangle(
                (getWidth() - image.getWidth()) / 2,
                (getHeight() - image.getHeight()) / 2,
                image.getWidth(),
                image.getHeight()
        );
        StartupUiUtil.drawImage(g, image, dstRect, srcRect, null);
    }

    public void dispose() {
        shutdown();
        if (image != null) {
            image.flush();
            image = null;
        }
    }

    private void shutdown() {
        if (this.executorService.isShutdown()) {
            return;
        }
        try {
            this.executorService.shutdownNow();
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public interface OnLoadListener {

        void onFail();

        void onGetInfo(int width, int height, int colorMode, long fileSize);
    }
}
