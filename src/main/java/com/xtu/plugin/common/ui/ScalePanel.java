package com.xtu.plugin.common.ui;

import com.intellij.openapi.util.SystemInfo;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;

public abstract class ScalePanel extends JPanel {

    private static final float MIN_SCALE_DIFFERENCE = 0.05f;

    protected float scale = 1.0f;

    public ScalePanel() {
        this.registerZoomEvent();
    }

    private void registerZoomEvent() {
        addMouseWheelListener(e -> {
            int modifiers = SystemInfo.isMac ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && (e.getModifiersEx() & modifiers) != 0) {
                float delta = e.getUnitsToScroll() * 0.1f;
                if (Math.abs(delta) < MIN_SCALE_DIFFERENCE) return;
                this.scale = Math.min(Math.max(0.1f, this.scale - delta), 10);
                onScaleChanged();
            }
        });
        requestFocus();
    }

    protected abstract void onScaleChanged();
}
