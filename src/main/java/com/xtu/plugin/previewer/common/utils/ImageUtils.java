package com.xtu.plugin.previewer.common.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageUtils {

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

    @Nullable
    public static BufferedImage read(@NotNull File file) throws IOException {
        if (!file.canRead()) throw new IIOException("Can't read input file!");
        ImageInputStream stream = ImageIO.createImageInputStream(file);
        if (stream == null) throw new IIOException("Can't create an ImageInputStream!");
        BufferedImage bi = read(stream);
        if (bi == null) stream.close();
        return bi;
    }

    @Nullable
    private static BufferedImage read(@NotNull ImageInputStream stream) throws IOException {
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(stream);
        while (iterator.hasNext()) {
            ImageReader reader = iterator.next();
            if (isTwelveMonkeysRead(reader)) return loadImage(stream, reader);
        }
        return null;
    }

    private static BufferedImage loadImage(@NotNull ImageInputStream stream, @NotNull ImageReader reader) throws IOException {
        ImageReadParam param = reader.getDefaultReadParam();
        reader.setInput(stream, true, true);
        BufferedImage bi;
        try (stream) {
            bi = reader.read(0, param);
        } finally {
            reader.dispose();
        }
        return bi;
    }

}
