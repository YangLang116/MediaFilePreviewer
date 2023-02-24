package com.xtu.plugin.previewer.common;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.xtu.plugin.previewer.common.ui.ImagePanel;
import com.xtu.plugin.previewer.common.utils.ImageUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public abstract class BaseBufferImageEditor extends UserDataHolderBase implements FileEditor {

    private final String name;
    private final VirtualFile file;
    private final String imageType;
    private ImagePanel imagePanel;

    public BaseBufferImageEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String imageType) {
        this.name = name;
        this.file = file;
        this.imageType = imageType;
    }

    @Override
    public @NotNull JComponent getComponent() {
        JPanel rootContainer = new JPanel(new BorderLayout());
        rootContainer.setBackground(UIUtil.isUnderDarcula() ? JBColor.black : JBColor.white);
        loadImageAsync(rootContainer);
        return rootContainer;
    }

    private void loadImageAsync(@NotNull JPanel rootContainer) {
        if (this.imagePanel != null) {
            rootContainer.add(this.imagePanel, BorderLayout.CENTER);
            return;
        }
        Application application = ApplicationManager.getApplication();
        application.executeOnPooledThread(() -> {
            ImageReader imageReader = ImageUtils.getTwelveMonkeysRead(imageType, file);
            application.invokeLater(() -> {
                this.imagePanel = new ImagePanel(imageType, file, imageReader);
                rootContainer.add(this.imagePanel, BorderLayout.CENTER);
            });
        });
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
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
        return file.isValid();
    }

    @Nullable
    @Override
    public VirtualFile getFile() {
        return file;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        if (this.imagePanel != null) this.imagePanel.dispose();
    }
}
