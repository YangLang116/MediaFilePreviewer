package com.xtu.plugin.common.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import javax.swing.*;

/**
 * Created by YangLang on 2017/11/25.
 */
public class ToastUtil {

    public static void make(MessageType type, String text) {
        Runnable showRunnable = () -> {
            JComponent rootPanel = WindowUtils.getVisibleRootPanel();
            if (rootPanel == null) return;
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(text, type, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .showInCenterOf(rootPanel);
        };
        Application application = ApplicationManager.getApplication();
        if (application.isDispatchThread()) {
            showRunnable.run();
        } else {
            application.invokeLater(showRunnable, ModalityState.any());
        }
    }
}