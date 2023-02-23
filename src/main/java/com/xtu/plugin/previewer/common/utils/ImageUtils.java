package com.xtu.plugin.previewer.common.utils;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
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

    public static String getImageInfo(String imageType, @NotNull BufferedImage image, @NotNull VirtualFile file) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final ColorModel colorModel = image.getColorModel();
        final int imageMode = colorModel.getPixelSize();
        String imageSize = StringUtil.formatFileSize(file.getLength());
        return String.format("%dx%d %s (%d-bit color) %s", width, height, imageType, imageMode, imageSize);
    }

    @Nullable
    public static BufferedImage[] readFile(@NotNull VirtualFile virtualFile) {
        String filePath = virtualFile.getPath();
        File imageFile = new File(filePath);
        if (!imageFile.canRead()) return null;
        try (ImageInputStream stream = ImageIO.createImageInputStream(imageFile)) {
            return readStream(stream);
        } catch (Exception e) {
            LogUtils.error("ImageUtils readFile: " + e.getMessage());
            return null;
        }
    }

    @Nullable
    private static BufferedImage[] readStream(@NotNull ImageInputStream stream) {
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(stream);
        while (iterator.hasNext()) {
            ImageReader reader = iterator.next();
            if (isTwelveMonkeysRead(reader)) return loadImage(stream, reader);
        }
        return null;
    }

    @Nullable
    private static BufferedImage[] loadImage(@NotNull ImageInputStream stream, @NotNull ImageReader reader) {
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(stream, false, true);
            int imageNum = reader.getNumImages(true);
            BufferedImage[] imageList = new BufferedImage[imageNum];
            for (int index = 0; index < imageNum; index++) {
                BufferedImage bufferedImage = reader.read(index, param);
                imageList[index] = bufferedImage;
            }
            return imageList;
        } catch (Exception e) {
            LogUtils.error("ImageUtils loadImage: " + e.getMessage());
            return null;
        } finally {
            reader.dispose();
        }
    }
}
