package com.xtu.plugin.common.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ColorUtils {

    @NotNull
    public static javafx.scene.paint.Color toFxColor(Color background) {
        double r = background.getRed() / 255.0;
        double g = background.getGreen() / 255.0;
        double b = background.getBlue() / 255.0;
        double a = background.getAlpha() / 255.0;
        return javafx.scene.paint.Color.color(r, g, b, a);
    }

    public static String toString(Color color) {
        return String.format("#%02x%02x%02x",
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );
    }
}
