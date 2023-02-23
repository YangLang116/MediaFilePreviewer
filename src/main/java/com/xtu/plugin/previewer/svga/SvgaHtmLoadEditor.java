package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseHtmLoadEditor;
import com.xtu.plugin.previewer.svga.generator.SvgaHtmlGenerator;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaHtmLoadEditor extends BaseHtmLoadEditor {

    public SvgaHtmLoadEditor(@NotNull String name, @NotNull VirtualFile file) {
        super(name, file);
        SvgaHtmlGenerator.generate(file, this::loadHtml);
    }
}
