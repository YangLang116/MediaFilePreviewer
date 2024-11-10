package com.xtu.plugin.previewer;

import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.messages.MessageBus;
import com.xtu.plugin.common.ui.FxHtmlLoader;
import com.xtu.plugin.common.utils.ColorUtils;
import com.xtu.plugin.common.utils.FileUtils;
import com.xtu.plugin.common.utils.LogUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class HtmlLoadEditor extends MediaFileEditor<JComponent> {

    private JBCefBrowser jcefBrowser;
    private FxHtmlLoader htmlLoader;

    public HtmlLoadEditor(@NotNull Project project,
                          @NotNull String editorName,
                          @NotNull VirtualFile file) {
        super(editorName, file);
        setComponent(createBrowser());
        refreshUI(project, file);
    }

    private JComponent createBrowser() {
        boolean supportJCEF = JBCefApp.isSupported();
        LogUtils.info("BaseFileEditor supportJCEF: " + supportJCEF);
        if (supportJCEF) {
            this.jcefBrowser = new JBCefBrowser();
            return this.jcefBrowser.getComponent();
        } else {
            this.htmlLoader = new FxHtmlLoader();
            return this.htmlLoader.getComponent();
        }
    }

    private void refreshUI(@NotNull Project project, @NotNull VirtualFile file) {
        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        messageBus.connect().subscribe(LafManagerListener.TOPIC, (LafManagerListener) lafManager -> load(project, file));
        load(project, file);
    }

    public abstract void load(@NotNull Project project, @NotNull VirtualFile file);

    public void setHtml(@NotNull String htmlContent) {
        if (this.jcefBrowser != null) {
            this.jcefBrowser.getComponent().setBackground(JBColor.background());
            this.jcefBrowser.loadHTML(htmlContent);
        } else {
            this.htmlLoader.setBackground(JBColor.background());
            this.htmlLoader.setHtml(htmlContent);
        }
    }

    public void showErrorTip(@NotNull String errorInfo) {
        String pageContent = FileUtils.loadTextFromResource("html/error.html")
                .replace("{error_info}", errorInfo)
                .replace("{body_color}", ColorUtils.toString(JBColor.background()))
                .replace("{main_color}", ColorUtils.toString(JBColor.foreground()));
        setHtml(pageContent);
    }

    @Override
    public void dispose() {
        if (this.jcefBrowser != null) {
            this.jcefBrowser.dispose();
        } else {
            this.htmlLoader.dispose();
        }
    }
}
