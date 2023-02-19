package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseFileEditor;
import com.xtu.plugin.previewer.svga.generator.SvgaHtmlGenerator;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaFileEditor extends BaseFileEditor {

    public SvgaFileEditor(@NotNull String name, @NotNull VirtualFile file) {
        super(name, file);
        SvgaHtmlGenerator.generate(file, this::loadHtml);
    }
}
