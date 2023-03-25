package com.xtu.plugin.common.ui;

import com.intellij.ui.javafx.JavaFxHtmlPanel;
import javafx.application.Platform;

public class MyFxHtmlPanel extends JavaFxHtmlPanel {

    public void setZoomLevel(double zoom) {
        Platform.runLater(() -> {
            if (myWebView == null) return;
            myWebView.setZoom(zoom);
        });
    }

    public double getZoomLevel() {
        if (myWebView == null) return 1.0;
        return myWebView.getZoom();
    }
}
