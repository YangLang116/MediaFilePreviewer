package com.xtu.plugin.common.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by YangLang on 2017/11/25.
 */
public class ToastUtil {

    public static void make(MessageType type, String text) {
        Runnable showRunnable = () -> {
            RelativePoint location = getLocation();
            if (location == null) return;
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(text, type, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(location, Balloon.Position.above);
        };
        Application application = ApplicationManager.getApplication();
        if (application.isDispatchThread()) {
            showRunnable.run();
        } else {
            application.invokeLater(showRunnable, ModalityState.any());
        }
    }

    @Nullable
    private static RelativePoint getLocation() {
        JComponent rootPanel = WindowUtils.getVisibleRootPanel();
        if (rootPanel == null) return null;
        Dimension size = rootPanel.getSize();
        Point point = new Point(size.width / 2, size.height - 50);
        return new RelativePoint(rootPanel, point);
    }
}