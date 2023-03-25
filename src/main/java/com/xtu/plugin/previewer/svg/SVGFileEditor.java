package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.BaseVolatileImageEditor;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditor extends BaseVolatileImageEditor {

    public SVGFileEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String fileExtension) {
        super(name, file, fileExtension);
    }
}
