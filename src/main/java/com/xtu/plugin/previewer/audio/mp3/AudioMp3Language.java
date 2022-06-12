package com.xtu.plugin.previewer.audio.mp3;

import com.intellij.lang.Language;

public final class AudioMp3Language extends Language {

    public static final AudioMp3Language INSTANCE = new AudioMp3Language();

    private AudioMp3Language() {
        super("mp3");
    }
}
