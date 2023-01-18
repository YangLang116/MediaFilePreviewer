package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseFileEditor;
import com.xtu.plugin.previewer.lottie.generator.LottieHtmlGenerator;
import org.jetbrains.annotations.NotNull;

public class LottieFileEditor extends BaseFileEditor {

    public LottieFileEditor(@NotNull VirtualFile file) {
        super("Lottie Editor", file);
        LottieHtmlGenerator.generate(file, this::loadHtml);
    }
}
