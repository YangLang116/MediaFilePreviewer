package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.MediaFileEditor;
import org.jetbrains.annotations.NotNull;

public class WebpFileEditor extends MediaFileEditor<WebpInfoPanel> {

    public WebpFileEditor(@NotNull String editorName, @NotNull VirtualFile file) {
        super(editorName, file);
        setComponent(new WebpInfoPanel(file));
    }

    @Override
    public void dispose() {
        super.dispose();
        getComponent().dispose();
    }
}
