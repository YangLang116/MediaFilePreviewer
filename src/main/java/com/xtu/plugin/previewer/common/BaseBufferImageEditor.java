package com.xtu.plugin.previewer.common;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.xtu.plugin.previewer.common.ui.ImagePanel;
import com.xtu.plugin.previewer.common.utils.ImageUtils;
import com.xtu.plugin.previewer.common.utils.LogUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;

public abstract class BaseBufferImageEditor extends UserDataHolderBase implements FileEditor {

    private final String name;
    private final String imageType;
    private final VirtualFile file;
    private BufferedImage[] imageList;

    public BaseBufferImageEditor(@NotNull String name, @NotNull String imageType, @NotNull VirtualFile file) {
        this.name = name;
        this.imageType = imageType;
        this.file = file;
    }

    @Override
    public @NotNull JComponent getComponent() {
        JPanel rootContainer = new JPanel(new BorderLayout());
        rootContainer.setBackground(UIUtil.isUnderDarcula() ? JBColor.black : JBColor.white);
        loadImageAsync(rootContainer);
        return rootContainer;
    }

    private void loadImageAsync(@NotNull JPanel rootContainer) {
        if (this.imageList != null) {
            displayImageList(rootContainer);
            return;
        }
        new Thread(() -> {
            this.imageList = ImageUtils.readFile(file);
            SwingUtilities.invokeLater(() -> displayImageList(rootContainer));
        }).start();
    }

    private void displayImageList(@NotNull JPanel rootContainer) {
        if (this.imageList == null || this.imageList.length == 0) {
            JComponent errorPanel = getErrorPanel();
            rootContainer.add(errorPanel, BorderLayout.CENTER);
        } else {
            LogUtils.info(imageType + " imageList size: " + this.imageList.length);
            BufferedImage bufferedImage = this.imageList[0];
            String imageInfo = ImageUtils.getImageInfo(imageType, bufferedImage, file);
            ImagePanel imagePanel = new ImagePanel(bufferedImage, imageInfo);
            rootContainer.add(imagePanel, BorderLayout.CENTER);
        }
    }

    private JComponent getErrorPanel() {
        String errorText = String.format("Fail to load %s Image", imageType);
        JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        return errorPanel;
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
        if (this.imageList != null) {
            LogUtils.info(imageType + " imageList clear");
            for (BufferedImage bufferedImage : this.imageList) {
                bufferedImage.getGraphics().dispose();
            }
            this.imageList = null;
        }
    }
}
