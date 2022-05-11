package com.xtu.plugin.previewer.common;

import com.intellij.openapi.diagnostic.Logger;

public class LogUtils {

    private static Logger getLogger() {
        return Logger.getInstance("MediaPreviewer -> ");
    }

    public static void info(String message) {
        getLogger().info(message);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

}
