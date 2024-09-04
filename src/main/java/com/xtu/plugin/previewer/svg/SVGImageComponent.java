package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.StartupUiUtil;
import com.twelvemonkeys.imageio.plugins.svg.SVGImageReader;
import com.twelvemonkeys.imageio.plugins.svg.SVGReadParam;
import com.xtu.plugin.common.ui.ScalePanel;
import com.xtu.plugin.common.utils.ImageUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SVGImageComponent extends ScalePanel {

    private static final int sDefaultSize = 100;

    private final VirtualFile svgFile;
    private final OnLoadListener listener;

    private boolean hasLoadInfo;
    private volatile BufferedImage image;

    public SVGImageComponent(@NotNull VirtualFile svgFile, @NotNull OnLoadListener listener) {
        super();
        this.svgFile = svgFile;
        this.listener = listener;
        this.render();
    }

    @Override
    protected void onScaleChanged() {
        this.render();
    }

    private void render() {
        Application application = ApplicationManager.getApplication();
        application.executeOnPooledThread(() -> {
            SVGImageReader reader = (SVGImageReader) ImageUtils.getTwelveMonkeysRead(svgFile);
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
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
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
        if (image != null) {
            image.flush();
            image = null;
        }
    }

    public interface OnLoadListener {

        void onFail();

        void onGetInfo(int width, int height, int colorMode, long fileSize);
    }
}
