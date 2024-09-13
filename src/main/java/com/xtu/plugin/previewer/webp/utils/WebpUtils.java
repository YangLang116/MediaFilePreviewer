package com.xtu.plugin.previewer.webp.utils;

import com.xtu.plugin.common.utils.LogUtils;
import com.xtu.plugin.previewer.webp.entity.WebPFrame;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageReader;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WebpUtils {

    @SuppressWarnings({"unchecked"})
    public static ArrayList<WebPFrame> loadAnimFrames(@NotNull ImageReader imageReader) {
        try {
            Class<?> webPImageReader = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageReader");
            Field framesField = webPImageReader.getDeclaredField("frames");
            framesField.setAccessible(true);
            List<Object> frameList = (List<Object>) framesField.get(imageReader);
            Class<?> frameClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.AnimationFrame");
            Field boundsField = frameClass.getDeclaredField("bounds");
            boundsField.setAccessible(true);
            Field durationField = frameClass.getDeclaredField("duration");
            durationField.setAccessible(true);
            ArrayList<WebPFrame> result = new ArrayList<>();
            for (Object o : frameList) {
                Rectangle rect = (Rectangle) boundsField.get(o);
                int duration = (int) durationField.get(o);
                result.add(new WebPFrame(rect, duration));
            }
            return result;
        } catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    public static Dimension getPicSize(@NotNull List<WebPFrame> frameList) {
        int width = 0;
        int height = 0;
        for (WebPFrame frame : frameList) {
            Rectangle bounds = frame.bounds;
            width = Math.max(width, bounds.width + bounds.x);
            height = Math.max(height, bounds.height + bounds.y);
        }
        return new Dimension(width, height);
    }

    public static Dimension getContentSize(@NotNull List<WebPFrame> frameList) {
        int imageWidth = 0;
        int imageHeight = 0;
        for (WebPFrame frame : frameList) {
            Rectangle bounds = frame.bounds;
            imageWidth = Math.max(imageWidth, bounds.width);
            imageHeight = Math.max(imageHeight, bounds.height);
        }
        return new Dimension(imageWidth, imageHeight);
    }
}
