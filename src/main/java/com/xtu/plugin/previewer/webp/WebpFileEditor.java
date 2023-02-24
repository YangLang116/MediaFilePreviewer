package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseBufferImageEditor;
import org.jetbrains.annotations.NotNull;

public class WebpFileEditor extends BaseBufferImageEditor {

    public WebpFileEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String imageType) {
        super(name, file, imageType);
    }

}
