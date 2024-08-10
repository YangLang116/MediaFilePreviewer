package com.xtu.plugin.common.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.javafx.JFXPanelWrapper;
import com.intellij.ui.scale.JBUIScale;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.WebPage;
import com.xtu.plugin.common.utils.ColorUtils;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FxHtmlLoader implements Disposable {

    private WebView webView;
    private JFXPanel myPanel;
    private final JPanel myPanelWrapper;

    private Color background;
    private final List<Runnable> delayRunList = new ArrayList<>();

    public FxHtmlLoader() {
        background = JBColor.background();
        myPanelWrapper = new JPanel(new BorderLayout());
        myPanelWrapper.setBackground(background);

        PlatformImpl.startup(() -> {
                    webView = new WebView();
                    webView.setContextMenuEnabled(false);
                    webView.setZoom(JBUIScale.scale(1.f));

                    final WebEngine engine = webView.getEngine();
                    engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue == Worker.State.RUNNING) {
                                    WebPage page = Accessor.getPageFor(engine);
                                    page.setBackgroundColor(background.getRGB());
                                }
                            }
                    );

                    javafx.scene.paint.Color fxColor = ColorUtils.toFxColor(background);
                    final Scene scene = new Scene(webView, fxColor);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        myPanel = new JFXPanelWrapper();

                        Platform.runLater(() -> myPanel.setScene(scene));

                        setHtml("");
                        for (Runnable action : delayRunList) {
                            Platform.runLater(action);
                        }
                        delayRunList.clear();

                        myPanelWrapper.add(myPanel, BorderLayout.CENTER);
                        myPanelWrapper.repaint();
                    });
                }
        );
    }

    public void setBackground(Color background) {
        this.background = background;
        myPanelWrapper.setBackground(background);
        javafx.scene.paint.Color fxColor = ColorUtils.toFxColor(background);
        runWhenAvailable(() -> myPanel.getScene().setFill(fxColor));
    }

    @NotNull
    public JComponent getComponent() {
        return myPanelWrapper;
    }

    public void setHtml(@NotNull String html) {
        runWhenAvailable(() -> this.webView.getEngine().loadContent(html));
    }

    @Override
    public void dispose() {
        runWhenAvailable(() -> this.webView.getEngine().load(null));
    }

    protected void runWhenAvailable(@NotNull Runnable runnable) {
        ApplicationManager.getApplication().assertIsDispatchThread();
        if (myPanel == null) {
            delayRunList.add(runnable);
        } else {
            Platform.runLater(runnable);
        }
    }
}
