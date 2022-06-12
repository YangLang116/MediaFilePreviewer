package com.xtu.plugin.previewer.audio.wav;

import com.intellij.lang.Language;

public final class AudioWavLanguage extends Language {

    public static final AudioWavLanguage INSTANCE = new AudioWavLanguage();

    private AudioWavLanguage() {
        super("wav");
    }
}
