package com.xtu.plugin.common.utils;

import com.intellij.ide.util.PropertiesComponent;

public class PluginUtils {

    private static final String KEY_AUTO_PLAY = "mp_auto_play";

    private static PropertiesComponent getStore() {
        return PropertiesComponent.getInstance();
    }

    public static boolean isAutoPlay() {
        return getStore().getBoolean(KEY_AUTO_PLAY, true);
    }

    public static void setAutoPlay(boolean autoPlay) {
        getStore().setValue(KEY_AUTO_PLAY, autoPlay);
    }
}
