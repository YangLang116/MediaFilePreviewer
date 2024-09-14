package com.xtu.plugin.common.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
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
        return loadImage(reader, imageIndex, null);
    }


    @Nullable
    public static BufferedImage loadImage(@NotNull ImageReader reader,
                                          int imageIndex,
                                          @Nullable ImageReadParam param) {
        try {
            return reader.read(imageIndex, param);
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    public static void dispose(@NotNull ImageReader reader) {
        Object readerInput = reader.getInput();
        if (readerInput instanceof Closeable) {
            CloseUtils.close((Closeable) readerInput);
        }
        reader.dispose();
    }
}
