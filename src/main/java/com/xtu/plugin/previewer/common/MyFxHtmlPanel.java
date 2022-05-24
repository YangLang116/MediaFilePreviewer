package com.xtu.plugin.previewer.common;

import com.intellij.ui.javafx.JavaFxHtmlPanel;
import javafx.scene.web.WebEngine;
import org.jetbrains.annotations.NotNull;

public class MyFxHtmlPanel extends JavaFxHtmlPanel {

    @Override
    protected void registerListeners(@NotNull WebEngine engine) {
        if (myWebView != null) {
            myWebView.setZoom(0.9);
        }
    }
}
