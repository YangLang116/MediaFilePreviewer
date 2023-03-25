package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.BaseVolatileImageEditorProvider;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditorProvider extends BaseVolatileImageEditorProvider {

    @Override
    public String getEditorName() {
        return "SVG Editor";
    }

    @Override
    public String getSupportExtension() {
        return "svg";
    }

    @Override
    public SVGFileEditor getEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String fileExtension) {
        return new SVGFileEditor(name, file, fileExtension);
    }
}
