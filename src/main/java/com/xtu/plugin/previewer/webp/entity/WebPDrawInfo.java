package com.xtu.plugin.previewer.webp.entity;


import java.awt.image.BufferedImage;

public class WebPDrawInfo {

    public final BufferedImage image;
    public final WebPFrame frame;

    public WebPDrawInfo(BufferedImage image, WebPFrame frame) {
        this.image = image;
        this.frame = frame;
    }
}