package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseFileEditor;
import com.xtu.plugin.previewer.lottie.generator.LottieHtmlGenerator;
import org.jetbrains.annotations.NotNull;

public class LottieFileEditor extends BaseFileEditor {

    public LottieFileEditor(@NotNull String name, @NotNull VirtualFile file) {
        super(name, file);
        LottieHtmlGenerator.generate(file, this::loadHtml);
    }
}
