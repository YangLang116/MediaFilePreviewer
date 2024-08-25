package com.xtu.plugin.common.utils;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.util.Iterator;

public class ImageUtils {

    private static boolean isTwelveMonkeysRead(@NotNull ImageReader reader) {
        ImageReaderSpi imageReaderSpi = reader.getOriginatingProvider();
        if (imageReaderSpi == null) return false;
        Class<? extends ImageReaderSpi> pClass = imageReaderSpi.getClass();
        String packageName = pClass.getPackageName();
        return packageName.startsWith("com.twelvemonkeys.imageio");
    }

    @Nullable
    public static ImageReader getTwelveMonkeysRead(@NotNull VirtualFile file) {
        File imageFile = new File(file.getPath());
        if (!imageFile.canRead()) return null;
        String fileExtension = file.getExtension();
        assert fileExtension != null;
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(fileExtension);
        while (iterator.hasNext()) {
            ImageReader reader = iterator.next();
            if (isTwelveMonkeysRead(reader)) return loadInputWithReader(reader, imageFile);
        }
        return null;
    }

    @Nullable
    private static ImageReader loadInputWithReader(@NotNull ImageReader reader, @NotNull File imageFile) {
        ImageInputStream stream = null;
        try {
            stream = ImageIO.createImageInputStream(imageFile);
            reader.setInput(stream, false, true);
            return reader;
        } catch (Throwable e) {
            CloseUtils.close(stream);
            LogUtils.info("ImageUtils loadInputWithReader: " + e.getMessage());
            return null;
        }
    }

    public static int getImageNum(@NotNull ImageReader imageReader) {
        try {
            return imageReader.getNumImages(true);
        } catch (Exception e) {
            LogUtils.info("WebpImagePanel initFirstFrameInThreadPool: " + e.getMessage());
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
        } catch (Throwable e) {
            LogUtils.info("ImageUtils loadImage: " + e.getMessage());
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
