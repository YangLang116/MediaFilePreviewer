package com.xtu.plugin.previewer.webp;

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

public class WebpInfoPanel extends JPanel implements WebpImageComponent.OnLoadListener {

    private final JLabel infoLabel;
    private final JLabel errorLabel;
    private final WebpImageComponent imageComponent;

    public WebpInfoPanel(@NotNull VirtualFile webpFile) {
        setLayout(new BorderLayout());
        setBackground(JBColor.background());
        this.infoLabel = attachInfoLabel();
        this.errorLabel = attachErrorLabel();
        this.imageComponent = attachImageComponent(webpFile);
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
        String errorText = "Fail to load WEBP Image";
        JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
        errorLabel.setForeground(JBColor.foreground());
        errorLabel.setVisible(false);
        add(errorLabel, BorderLayout.CENTER);
        return errorLabel;
    }

    private WebpImageComponent attachImageComponent(@NotNull VirtualFile webpFile) {
        WebpImageComponent image = new WebpImageComponent(webpFile, this);
        add(image, BorderLayout.CENTER);
        return image;
    }

    @Override
    public void onSuccess(@NotNull Dimension size, int colorMode, int imageNum, long fileSize) {
        String imageInfo = String.format("%dx%d WebP (%d-bit color%s) %s",
                size.width, size.height,
                colorMode,
                imageNum == 1 ? "" : String.format(" / %d frames", imageNum),
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
