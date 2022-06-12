package com.xtu.plugin.previewer.audio.wav;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.PluginIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AudioWavFileType extends LanguageFileType {

    public static final AudioWavFileType INSTANCE = new AudioWavFileType();

    private AudioWavFileType() {
        super(AudioWavLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "wav";
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "wav file";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "WAV file";
    }

    @Override
    public @Nullable Icon getIcon() {
        return PluginIcons.AUDIO_ICON;
    }
}
