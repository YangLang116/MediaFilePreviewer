package com.xtu.plugin.common.utils;

import java.awt.*;

public class ColorUtils {

    public static String toString(Color color) {
        return String.format("#%02x%02x%02x",
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );
    }
}
