package com.xtu.plugin.previewer.webp.utils;

import com.twelvemonkeys.imageio.plugins.webp.MFAnimationFrame;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class WebpUtils {

    public static Dimension getPicSize(@NotNull List<MFAnimationFrame> frameList) {
        int width = 0;
        int height = 0;
        for (MFAnimationFrame frame : frameList) {
            Rectangle bounds = frame.bounds;
            width = Math.max(width, bounds.width + bounds.x);
            height = Math.max(height, bounds.height + bounds.y);
        }
        return new Dimension(width, height);
    }

    public static Dimension getContentSize(@NotNull List<MFAnimationFrame> frameList) {
        int imageWidth = 0;
        int imageHeight = 0;
        for (MFAnimationFrame frame : frameList) {
            Rectangle bounds = frame.bounds;
            imageWidth = Math.max(imageWidth, bounds.width);
            imageHeight = Math.max(imageHeight, bounds.height);
        }
        return new Dimension(imageWidth, imageHeight);
    }
}
