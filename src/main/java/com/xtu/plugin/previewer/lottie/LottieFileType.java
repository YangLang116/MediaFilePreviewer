package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsSafe;
import icons.PluginIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LottieFileType extends LanguageFileType {

    public static LottieFileType INSTANCE = new LottieFileType();

    private LottieFileType() {
        super(LottieLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "lottie file";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Lottie animation file";
    }

    @Override
    @NlsSafe
    @NotNull
    public String getDefaultExtension() {
        return "json";
    }

    @Override
    @Nullable
    public Icon getIcon() {
        return PluginIcons.LOTTIE_ICON;
    }
}
