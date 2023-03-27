package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBBox;
import com.intellij.util.ui.StartupUiUtil;
import com.xtu.plugin.common.utils.ImageUtils;
import com.xtu.plugin.common.utils.LogUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class SVGImagePanel extends JPanel {

    private float curScale = 1.0f;
    private BufferedImage bufferedImage;

    public SVGImagePanel(@NotNull VirtualFile file, @NotNull String fileExtension) {
        this.setLayout(new BorderLayout());
        this.registerKeyBoardEvent();
        this.render(file, fileExtension);
    }

    private void render(@NotNull VirtualFile file, @NotNull String fileExtension) {
        Application application = ApplicationManager.getApplication();
        application.executeOnPooledThread(() -> {
            ImageReader imageReader = ImageUtils.getTwelveMonkeysRead(fileExtension, file);
            if (imageReader == null) {
                LogUtils.info("SVGImagePanel imageReader == null");
                this.showErrorLabel(fileExtension);
                return;
            }
            BufferedImage image = ImageUtils.loadImage(imageReader, 0);
            ImageUtils.dispose(imageReader);
            if (image == null) {
                LogUtils.info("SVGImagePanel bufferedImage == null");
                this.showErrorLabel(fileExtension);
                return;
            }
            showImageInfoLabel(this.bufferedImage = image, file, fileExtension);
        });
    }

    private void showImageInfoLabel(@NotNull BufferedImage bufferedImage,
                                    @NotNull VirtualFile file,
                                    @NotNull String fileExtension) {
        final String ImageInfo = ImageUtils.getImageInfo(bufferedImage, file, fileExtension);
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            JBBox container = JBBox.createHorizontalBox();
            container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            container.add(JBBox.createHorizontalGlue());
            JLabel imageInfoLabel = new JLabel();
            imageInfoLabel.setText(ImageInfo);
            container.add(imageInfoLabel);
            add(container, BorderLayout.NORTH);
        });
    }

    private void showErrorLabel(@NotNull String fileExtension) {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            String errorText = String.format("Fail to load %s Image", fileExtension);
            JLabel errorLabel = new JLabel(errorText, Messages.getErrorIcon(), SwingConstants.CENTER);
            add(errorLabel, BorderLayout.CENTER);
        });
    }


    public void registerKeyBoardEvent() {
        int maskKey = InputEvent.SHIFT_DOWN_MASK;
        registerKeyboardAction(e -> {
            this.curScale = curScale + 0.1f;  //放大
            repaint();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_W, maskKey), JComponent.WHEN_IN_FOCUSED_WINDOW);

        registerKeyboardAction(e -> {
            this.curScale = curScale - 0.1f;//缩小
            repaint();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, maskKey), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (bufferedImage == null) return;
        int imageWidth = bufferedImage.getWidth(null);
        int imageHeight = bufferedImage.getHeight(null);
        int displayWidth = (int) (imageWidth * curScale);
        int displayHeight = (int) (imageHeight * curScale);
        int displayX = (getWidth() - displayWidth) / 2;
        int displayY = (getHeight() - displayHeight) / 2;
        Rectangle srcRect = new Rectangle(0, 0, imageWidth, imageHeight);
        Rectangle dstRect = new Rectangle(displayX, displayY, displayWidth, displayHeight);
        StartupUiUtil.drawImage(g, bufferedImage, dstRect, srcRect, null);
    }

    public void dispose() {
        if (bufferedImage != null) {
            bufferedImage.flush();
            bufferedImage = null;
        }
    }
}
