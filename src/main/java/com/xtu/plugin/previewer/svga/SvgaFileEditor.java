package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseFileEditor;
import com.xtu.plugin.previewer.svga.generator.SvgaHtmlGenerator;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaFileEditor extends BaseFileEditor {

    public SvgaFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        super("SVGA Editor", project, file);
        SvgaHtmlGenerator.generate(file, this::loadHtml);
    }
}
