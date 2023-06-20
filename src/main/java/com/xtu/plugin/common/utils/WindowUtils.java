package com.xtu.plugin.common.utils;

import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class WindowUtils {

    @Nullable
    public static JComponent getVisibleRootPanel() {
        WindowManager windowManager = WindowManager.getInstance();
        JFrame visibleFrame = windowManager.findVisibleFrame();
        if (visibleFrame == null) return null;
        return visibleFrame.getRootPane();
    }
}
