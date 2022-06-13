package com.xtu.plugin.previewer.video.mp4;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.PluginIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public final class VideoMp4FileType extends LanguageFileType {

    public static final VideoMp4FileType INSTANCE = new VideoMp4FileType();

    private VideoMp4FileType() {
        super(VideoMp4Language.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "mp4 file";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "MP4 file";
    }

    @Override
    @NotNull
    public String getDefaultExtension() {
        return "mp4";
    }

    @Override
    public @Nullable Icon getIcon() {
        return PluginIcons.VIDEO_ICON;
    }
}
