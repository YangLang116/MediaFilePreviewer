package com.xtu.plugin.previewer.common.ui;

import com.intellij.ui.components.JBBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    public ImagePanel(@NotNull BufferedImage image, @NotNull String imageInfo) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(createLabel(imageInfo), BorderLayout.NORTH);
        add(new ImageComponent(image), BorderLayout.CENTER);
    }

    private static JComponent createLabel(@NotNull String imageInfo) {
        JBBox container = JBBox.createHorizontalBox();
        container.add(JBBox.createHorizontalGlue());
        container.add(new JLabel(imageInfo));
        return container;
    }

    private static class ImageComponent extends JComponent {

        private static final float MAX_SIZE = 400.0f;

        private final BufferedImage image;
        private final Dimension preferredSize;
        private float curScale = 1.0f;

        private ImageComponent(@NotNull BufferedImage image) {
            this.image = image;
            int width = image.getWidth();
            int height = image.getHeight();
            if (width > MAX_SIZE || height > MAX_SIZE) {
                final float factor = MAX_SIZE / Math.max(width, height);
                this.preferredSize = new Dimension((int) (width * factor), (int) (height * factor));
            } else {
                this.preferredSize = new Dimension(width, height);
            }
            registerKeyBoardEvent();
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
        public void paint(final Graphics g) {
            super.paint(g);
            final int imgWidth = (int) (preferredSize.width * curScale);
            final int imgHeight = (int) (preferredSize.height * curScale);
            final int width = getWidth();
            final int height = getHeight();
            g.drawImage(image, (width - imgWidth) / 2, (height - imgHeight) / 2,
                    imgWidth, imgHeight, this);
        }
    }

}
