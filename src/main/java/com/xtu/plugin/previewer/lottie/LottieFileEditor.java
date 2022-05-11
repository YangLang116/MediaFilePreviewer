package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseFileEditor;
import com.xtu.plugin.previewer.lottie.generator.LottieHtmlGenerator;
import org.jetbrains.annotations.NotNull;

public class LottieFileEditor extends BaseFileEditor {

    public LottieFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        super("Lottie Editor", project, file);
        LottieHtmlGenerator.generate(file, this::loadHtml);
    }
}
