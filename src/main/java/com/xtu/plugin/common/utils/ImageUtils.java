package com.xtu.plugin.common.utils;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.Closeable;
import java.io.File;
import java.util.Iterator;

public class ImageUtils {

    @SuppressWarnings("SpellCheckingInspection")
    private static boolean isTwelveMonkeysRead(@NotNull ImageReader reader) {
        ImageReaderSpi imageReaderSpi = reader.getOriginatingProvider();
        if (imageReaderSpi == null) return false;
        Class<? extends ImageReaderSpi> pClass = imageReaderSpi.getClass();
        if (pClass == null) return false;
        String packageName = pClass.getPackageName();
        return packageName.startsWith("com.twelvemonkeys.imageio");
    }

    public static boolean checkSupport(@NotNull String extension) {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(extension);
        while (iterator.hasNext()) {
            ImageReader reader = iterator.next();
            if (isTwelveMonkeysRead(reader)) return true;
        }
        return false;
    }

    public static String getImageInfo(@NotNull BufferedImage image, @NotNull VirtualFile file, String fileExtension) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final ColorModel colorModel = image.getColorModel();
        final int imageMode = colorModel.getPixelSize();
        String imageSize = StringUtil.formatFileSize(file.getLength());
        return String.format("%dx%d %s (%d-bit color) %s", width, height, fileExtension, imageMode, imageSize);
    }

    @Nullable
    public static ImageReader getTwelveMonkeysRead(@NotNull String fileExtension, @NotNull VirtualFile file) {
        String filePath = file.getPath();
        File imageFile = new File(filePath);
        if (!imageFile.canRead()) return null;
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

    @Nullable
    public static BufferedImage loadImage(@NotNull ImageReader reader, int imageIndex) {
        try {
            return reader.read(imageIndex);
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
