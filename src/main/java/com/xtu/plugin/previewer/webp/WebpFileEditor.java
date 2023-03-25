package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.BaseVolatileImageEditor;
import org.jetbrains.annotations.NotNull;

public class WebpFileEditor extends BaseVolatileImageEditor {

    public WebpFileEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String fileExtension) {
        super(name, file, fileExtension);
    }

}
