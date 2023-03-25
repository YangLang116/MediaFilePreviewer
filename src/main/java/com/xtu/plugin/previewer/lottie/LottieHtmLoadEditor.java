package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.BaseHtmLoadEditor;
import com.xtu.plugin.previewer.lottie.generator.LottieHtmlGenerator;
import org.jetbrains.annotations.NotNull;

public class LottieHtmLoadEditor extends BaseHtmLoadEditor {

    public LottieHtmLoadEditor(@NotNull String name, @NotNull VirtualFile file) {
        super(name, file);
        LottieHtmlGenerator.generate(file, this::loadHtml);
    }
}
