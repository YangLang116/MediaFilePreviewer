package com.xtu.plugin.previewer.video.mp4;

import com.intellij.lang.Language;

public final class VideoMp4Language extends Language {

    public static final VideoMp4Language INSTANCE = new VideoMp4Language();

    private VideoMp4Language() {
        super("mp4");
    }
}
