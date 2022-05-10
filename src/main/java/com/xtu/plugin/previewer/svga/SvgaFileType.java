package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.PluginIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaFileType extends LanguageFileType {

    public static final SvgaFileType INSTANCE = new SvgaFileType();

    private SvgaFileType() {
        super(SvgaLanguage.INSTANCE);
    }

    @Override
    @NonNls
    @NotNull
    public String getName() {
        return "svga file";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Svga Animation File";
    }

    @Override
    @NotNull
    public String getDefaultExtension() {
        return "svga";
    }

    @Override
    public @Nullable Icon getIcon() {
        return PluginIcons.SVGA_ICON;
    }
}
