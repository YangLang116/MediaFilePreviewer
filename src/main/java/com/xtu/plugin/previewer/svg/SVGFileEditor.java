package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseBufferImageEditor;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditor extends BaseBufferImageEditor {

    public SVGFileEditor(@NotNull String name, @NotNull VirtualFile file) {
        super(name, "SVG", file);
    }
}
