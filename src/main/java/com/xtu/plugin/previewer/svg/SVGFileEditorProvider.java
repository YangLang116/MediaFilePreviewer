package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseBufferImageEditor;
import com.xtu.plugin.previewer.common.BaseBufferImageEditorProvider;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditorProvider extends BaseBufferImageEditorProvider {

    @Override
    public String getEditorName() {
        return "SVG Editor";
    }

    @Override
    public String getSupportExtension() {
        return "svg";
    }

    @Override
    public BaseBufferImageEditor getEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String imageType) {
        return new SVGFileEditor(name, file, imageType);
    }
}
