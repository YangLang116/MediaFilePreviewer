package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.BaseBufferImageEditor;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditor extends BaseBufferImageEditor {

    public SVGFileEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String imageType) {
        super(name, file, imageType);
    }
}
