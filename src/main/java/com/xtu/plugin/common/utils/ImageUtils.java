package com.xtu.plugin.common.utils;

import com.intellij.util.ui.StartupUiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.Closeable;

public class ImageUtils {

    public static int getImageNum(@NotNull ImageReader imageReader) {
        try {
            return imageReader.getNumImages(true);
        } catch (Exception e) {
            LogUtils.error(e);
            return 0;
        }
    }

    @Nullable
    public static BufferedImage loadImage(@NotNull ImageReader reader, int imageIndex) {
        return loadImage(reader, null, imageIndex);
    }


    @Nullable
    public static BufferedImage loadImage(@NotNull ImageReader reader, @Nullable ImageReadParam param, int imageIndex) {
        try {
            return reader.read(imageIndex, param);
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    public static void dispose(@Nullable ImageReader reader) {
        if (reader == null) return;
        Object readerInput = reader.getInput();
        if (readerInput instanceof Closeable) {
            CloseUtils.close((Closeable) readerInput);
        }
        reader.dispose();
    }

    public static VolatileImage createVolatileImage(@NotNull Dimension size) {
        int width = size.width;
        int height = size.height;
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration config = defaultScreenDevice.getDefaultConfiguration();
        VolatileImage image;
        try {
            ImageCapabilities capabilities = new ImageCapabilities(true);
            image = config.createCompatibleVolatileImage(width, height, capabilities, Transparency.TRANSLUCENT);
        } catch (Exception e) {
            LogUtils.error(e);
            image = config.createCompatibleVolatileImage(width, height, Transparency.TRANSLUCENT);
        }
        image.validate(null);
        image.setAccelerationPriority(1F);
        return image;
    }

    public static void drawImageToVolatileImage(@NotNull VolatileImage volatileImage, @NotNull BufferedImage image) {
        Graphics2D g2d = volatileImage.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        updateGraphicsHint(g2d, true);
        Rectangle rect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        StartupUiUtil.drawImage(g2d, image, rect, rect, null);
        g2d.dispose();
        image.flush();
    }

    public static void updateGraphicsHint(@NotNull Graphics2D g, boolean speedFirst) {
        if (speedFirst) {
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        } else {
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
    }
}
