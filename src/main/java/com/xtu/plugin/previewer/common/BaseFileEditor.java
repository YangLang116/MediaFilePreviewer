package com.xtu.plugin.previewer.common;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.javafx.JavaFxHtmlPanel;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class BaseFileEditor extends UserDataHolderBase implements FileEditor {

    private final String name;
    private final Project project;
    private final VirtualFile file;

    private JBCefBrowser jcefBrowser;
    private JavaFxHtmlPanel htmlPanel;

    public BaseFileEditor(@NotNull String name,
                          @NotNull Project project,
                          @NotNull VirtualFile file) {
        this.name = name;
        this.project = project;
        this.file = file;
        this.initBrowser();
    }

    private void initBrowser() {
        boolean supportJCEF = JBCefApp.isSupported();
        LogUtils.info("BaseFileEditor supportJCEF: " + supportJCEF);
        if (supportJCEF) {
            this.jcefBrowser = new JBCefBrowser();
            this.jcefBrowser.getComponent().setBackground(UIUtil.isUnderDarcula() ? JBColor.BLACK : JBColor.WHITE);
        } else {
            this.htmlPanel = new MyFxHtmlPanel();
            this.htmlPanel.setBackground(UIUtil.isUnderDarcula() ? JBColor.BLACK : JBColor.WHITE);
        }
    }

    public void loadHtml(@NotNull String htmlContent) {
        if (this.jcefBrowser != null) {
            this.jcefBrowser.loadHTML(htmlContent);
        } else {
            this.htmlPanel.setHtml(htmlContent);
        }
    }

    @Override
    public @NotNull JComponent getComponent() {
        if (this.jcefBrowser != null) {
            return this.jcefBrowser.getComponent();
        } else {
            return this.htmlPanel.getComponent();
        }
    }

    @Override
    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return getComponent();
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return this.file.isValid();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Override
    @Nullable
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        if (this.jcefBrowser != null) {
            this.jcefBrowser.dispose();
        } else {
            this.htmlPanel.dispose();
        }
    }
}
