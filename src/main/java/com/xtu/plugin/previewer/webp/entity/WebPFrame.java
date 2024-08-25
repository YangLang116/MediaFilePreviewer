package com.xtu.plugin.previewer.webp.entity;

import java.awt.*;

public class WebPFrame {

    public final Rectangle bounds;
    public final int duration;

    public WebPFrame(Rectangle bounds, int duration) {
        this.bounds = bounds;
        this.duration = duration;
    }
}
