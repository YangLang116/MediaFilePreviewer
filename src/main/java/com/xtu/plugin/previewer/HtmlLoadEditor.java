package com.xtu.plugin.previewer;

import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.messages.MessageBus;
import com.xtu.plugin.common.utils.ColorUtils;
import com.xtu.plugin.common.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class HtmlLoadEditor extends MediaFileEditor<JComponent> {

    private JBCefBrowser browser;

    public HtmlLoadEditor(@NotNull Project project,
                          @NotNull String editorName,
                          @NotNull VirtualFile file) {
        super(editorName, file);
        initBrowser();
        refreshUI(project, file);
    }

    private void initBrowser() {
        this.browser = new JBCefBrowser();
        setComponent(this.browser.getComponent());
    }

    private void refreshUI(@NotNull Project project, @NotNull VirtualFile file) {
        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        messageBus.connect().subscribe(LafManagerListener.TOPIC, (LafManagerListener) lafManager -> load(project, file));
        load(project, file);
    }

    public abstract void load(@NotNull Project project, @NotNull VirtualFile file);

    public void setHtml(@NotNull String htmlContent) {
        this.browser.getComponent().setBackground(JBColor.background());
        this.browser.loadHTML(htmlContent);
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
        this.browser.dispose();
    }
}
