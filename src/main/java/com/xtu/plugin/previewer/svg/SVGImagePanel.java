package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.Formats;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBBox;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.StartupUiUtil;
import com.twelvemonkeys.imageio.plugins.svg.SVGImageReader;
import com.twelvemonkeys.imageio.plugins.svg.SVGReadParam;
import com.xtu.plugin.common.ui.ScalePanel;
import com.xtu.plugin.common.utils.ImageUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SVGImagePanel extends ScalePanel {

    private static final int sDefaultSize = 100;

    private final VirtualFile svgFile;

    private BufferedImage image;
    private JLabel infoLabel;
    private JLabel errorLabel;

    public SVGImagePanel(@NotNull VirtualFile svgFile) {
        super();
        this.svgFile = svgFile;
        this.setLayout(new BorderLayout());
        this.render();
    }

    private void showImageInfo(int width, int height, @NotNull BufferedImage image) {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            if (this.infoLabel == null) {
                String info = String.format("%dx%d SVG (%d-bit color) %s",
                        width, height,
                        image.getColorModel().getPixelSize(),
                        Formats.formatFileSize(svgFile.getLength())
                );
                JBBox container = JBBox.createHorizontalBox();
                container.setBorder(JBUI.Borders.empty(10));
                container.add(JBBox.createHorizontalGlue());
                container.add(this.infoLabel = new JLabel(info));
                add(container, BorderLayout.NORTH);
            }
            if (this.errorLabel != null && this.errorLabel.getParent() != null) {
                remove(this.errorLabel);
            }
            this.image = image;
            repaint();
        });
    }

    private void showErrorInfo() {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            if (this.errorLabel == null) {
                String errorText = "Fail to load SVG Image";
                this.errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
            }
            if (this.errorLabel.getParent() == null) {
                add(this.errorLabel, BorderLayout.CENTER);
            }
            this.image = null;
            repaint();
        });
    }

    @Override
    protected void onScaleChanged() {
        this.render();
    }

    private void render() {
        Application application = ApplicationManager.getApplication();
        application.executeOnPooledThread(() -> {
            SVGImageReader reader = (SVGImageReader) ImageUtils.getTwelveMonkeysRead(svgFile);
            if (reader == null) {
                showErrorInfo();
                return;
            }
            try {
                SVGReadParam param = new SVGReadParam();
                int displaySize = (int) (sDefaultSize * scale);
                param.setSourceRenderSize(new Dimension(displaySize, displaySize));
                BufferedImage image = ImageUtils.loadImage(reader, 0, param);
                if (image == null) {
                    showErrorInfo();
                } else {
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    showImageInfo(width, height, image);
                }
            } catch (Exception e) {
                showErrorInfo();
            } finally {
                ImageUtils.dispose(reader);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image == null) {
            g.clearRect(0, 0, getWidth(), getHeight());
            return;
        }
        Rectangle srcRect = new Rectangle(
                0, 0,
                image.getWidth(),
                image.getHeight()
        );
        Rectangle dstRect = new Rectangle(
                (getWidth() - image.getWidth()) / 2,
                (getHeight() - image.getHeight()) / 2,
                image.getWidth(),
                image.getHeight()
        );
        StartupUiUtil.drawImage(g, image, dstRect, srcRect, null);
    }

    public void dispose() {
        if (image != null) {
            image.flush();
            image = null;
        }
    }
}
