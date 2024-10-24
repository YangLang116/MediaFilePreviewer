package com.twelvemonkeys.imageio.plugins.webp;

import java.awt.*;

/**
 * Represents one animation frame (ANMF) chunk.
 */
public final class MFAnimationFrame extends RIFFChunk {

    public final Rectangle bounds;
    public final int duration;
    public final boolean blend;
    public final boolean dispose;

    public MFAnimationFrame(long length, long offset, Rectangle rectangle, int duration, int flags) {
        super(WebP.CHUNK_ANMF, length, offset);

        this.bounds = rectangle.getBounds();
        this.duration = duration; // Duration in ms

        blend = (flags & 2) == 0; // 0: Use alpha blending (SrcOver), 1: Do not blend (Src)
        dispose = (flags & 1) != 0; // 0: Do not dispose, 1: Dispose to (fill bounds with) background color
    }
}