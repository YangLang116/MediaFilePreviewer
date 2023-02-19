package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.util.text.StringUtil;
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
import java.awt.image.ColorModel;
import java.beans.PropertyChangeListener;
import java.io.File;

public class WebpFileEditor extends UserDataHolderBase implements FileEditor {

    private final String name;
    private final VirtualFile file;

    public WebpFileEditor(@NotNull String name, @NotNull VirtualFile file) {
        this.name = name;
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
        new Thread(() -> {
            BufferedImage image = loadImage();
            SwingUtilities.invokeLater(() -> {
                if (image == null) {
                    JComponent errorPanel = getErrorPanel();
                    rootContainer.add(errorPanel, BorderLayout.CENTER);
                } else {
                    String imageInfo = getImageInfo(image);
                    ImagePanel imagePanel = new ImagePanel(image, imageInfo);
                    rootContainer.add(imagePanel, BorderLayout.CENTER);
                }
            });
        }).start();
    }

    private JComponent getErrorPanel() {
        JLabel errorLabel = new JLabel("Fail to load WebP Image", Messages.getErrorIcon(), SwingConstants.CENTER);
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        return errorPanel;
    }

    private String getImageInfo(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final ColorModel colorModel = image.getColorModel();
        final int imageMode = colorModel.getPixelSize();
        String imageSize = StringUtil.formatFileSize(file.getLength());
        return String.format("%dx%d WEBP (%d-bit color) %s", width, height, imageMode, imageSize);
    }

    private BufferedImage loadImage() {
        try {
            String filePath = file.getPath();
            File imageFile = new File(filePath);
            return ImageUtils.read(imageFile);
        } catch (Exception e) {
            LogUtils.error("WebpFileEditor loadImage: " + e.getMessage());
            return null;
        }
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
    }
}
