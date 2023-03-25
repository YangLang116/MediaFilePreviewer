package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.BaseBufferImageEditor;
import com.xtu.plugin.common.BaseBufferImageEditorProvider;
import org.jetbrains.annotations.NotNull;

public class WebpFileEditorProvider extends BaseBufferImageEditorProvider {

    @Override
    public String getEditorName() {
        return "Webp Editor";
    }

    @Override
    public String getSupportExtension() {
        return "webp";
    }

    @Override
    public BaseBufferImageEditor getEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String imageType) {
        return new WebpFileEditor(name, file, imageType);
    }
}
