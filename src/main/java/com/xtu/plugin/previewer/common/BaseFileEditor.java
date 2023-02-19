package com.xtu.plugin.previewer.common;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.ui.UIUtil;
import com.xtu.plugin.previewer.common.ui.MyFxHtmlPanel;
import com.xtu.plugin.previewer.common.utils.LogUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

public class BaseFileEditor extends UserDataHolderBase implements FileEditor {

    private final String name;
    private final VirtualFile file;

    private JBCefBrowser jcefBrowser;
    private MyFxHtmlPanel htmlPanel;

    public BaseFileEditor(@NotNull String name, @NotNull VirtualFile file) {
        this.name = name;
        this.file = file;
        this.initBrowser();
    }

    private void initBrowser() {
        boolean supportJCEF = JBCefApp.isSupported();
        LogUtils.info("BaseFileEditor supportJCEF: " + supportJCEF);
        if (supportJCEF) {
            this.jcefBrowser = new JBCefBrowser();
            JComponent jcefComponent = this.jcefBrowser.getComponent();
            jcefComponent.setBackground(UIUtil.isUnderDarcula() ? JBColor.BLACK : JBColor.WHITE);
            registerKeyBoardEvent(jcefComponent, jcefBrowser::getZoomLevel, jcefBrowser::setZoomLevel);
        } else {
            this.htmlPanel = new MyFxHtmlPanel();
            this.htmlPanel.setBackground(UIUtil.isUnderDarcula() ? JBColor.BLACK : JBColor.WHITE);
            JComponent htmlPanelComponent = htmlPanel.getComponent();
            registerKeyBoardEvent(htmlPanelComponent, htmlPanel::getZoomLevel, htmlPanel::setZoomLevel);
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

    @Nullable
    @Override
    public VirtualFile getFile() {
        return file;
    }

    @Override
    public void dispose() {
        if (this.jcefBrowser != null) {
            this.jcefBrowser.dispose();
        } else {
            this.htmlPanel.dispose();
        }
    }

    public void registerKeyBoardEvent(@NotNull JComponent component,
                                      @NotNull ZoomValueProvider zoomValueProvider,
                                      @NotNull ZoomValueSetter zoomValueSetter) {
        int maskKey = InputEvent.SHIFT_DOWN_MASK;
        //放大
        component.registerKeyboardAction(e -> {
            double currentZoom = zoomValueProvider.getZoom();
            zoomValueSetter.setZoom(currentZoom + 0.1D);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_W, maskKey), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //缩小
        component.registerKeyboardAction(e -> {
            double currentZoom = zoomValueProvider.getZoom();
            zoomValueSetter.setZoom(currentZoom - 0.1D);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, maskKey), JComponent.WHEN_IN_FOCUSED_WINDOW);
        component.requestFocus();
    }

    private interface ZoomValueProvider {
        double getZoom();
    }

    private interface ZoomValueSetter {
        void setZoom(double zoom);
    }
}
