package com.xtu.plugin.previewer.webp.entity;


import com.twelvemonkeys.imageio.plugins.webp.MFAnimationFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

public final class WebPDrawFrame {

    public final BufferedImage image;
    public final MFAnimationFrame frame;

    public WebPDrawFrame(@Nullable BufferedImage image, @NotNull MFAnimationFrame frame) {
        this.image = image;
        this.frame = frame;
    }
}