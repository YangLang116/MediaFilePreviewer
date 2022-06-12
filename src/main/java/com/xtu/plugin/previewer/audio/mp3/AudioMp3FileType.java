package com.xtu.plugin.previewer.audio.mp3;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.PluginIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AudioMp3FileType extends LanguageFileType {

    public static final AudioMp3FileType INSTANCE = new AudioMp3FileType();

    private AudioMp3FileType() {
        super(AudioMp3Language.INSTANCE);
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "mp3";
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "mp3 file";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "MP3 file";
    }

    @Override
    public @Nullable Icon getIcon() {
        return PluginIcons.AUDIO_ICON;
    }
}
