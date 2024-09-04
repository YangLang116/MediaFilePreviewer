package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.Formats;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBBox;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class SVGInfoPanel extends JPanel implements SVGImageComponent.OnLoadListener {

    private final JLabel infoLabel;
    private final JLabel errorLabel;
    private final SVGImageComponent imageComponent;

    public SVGInfoPanel(@NotNull VirtualFile svgFile) {
        setLayout(new BorderLayout());
        setBackground(JBColor.background());
        this.infoLabel = attachInfoLabel();
        this.errorLabel = attachErrorLabel();
        this.imageComponent = attachImageComponent(svgFile);
    }

    private JLabel attachInfoLabel() {
        JBBox container = JBBox.createHorizontalBox();
        container.setBorder(JBUI.Borders.empty(10));
        container.add(JBBox.createHorizontalGlue());
        JLabel infoLabel = new JLabel();
        infoLabel.setForeground(JBColor.foreground());
        container.add(infoLabel);
        add(container, BorderLayout.NORTH);
        return infoLabel;
    }

    private JLabel attachErrorLabel() {
        String errorText = "Fail to load SVG Image";
        JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
        errorLabel.setForeground(JBColor.foreground());
        errorLabel.setVisible(false);
        add(errorLabel, BorderLayout.CENTER);
        return errorLabel;
    }

    private SVGImageComponent attachImageComponent(@NotNull VirtualFile svgFile) {
        SVGImageComponent image = new SVGImageComponent(svgFile, this);
        add(image, BorderLayout.CENTER);
        return image;
    }

    @Override
    public void onGetInfo(int width, int height, int colorMode, long fileSize) {
        String imageInfo = String.format("%dx%d SVG (%d-bit color) %s",
                width, height,
                colorMode,
                Formats.formatFileSize(fileSize)
        );
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> this.infoLabel.setText(imageInfo));
    }

    @Override
    public void onFail() {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> this.errorLabel.setVisible(true));
    }

    public void dispose() {
        this.imageComponent.dispose();
    }
}
