package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.MediaFileEditor;
import org.jetbrains.annotations.NotNull;

public class WebpFileEditor extends MediaFileEditor {

    public WebpFileEditor(@NotNull String editorName, @NotNull VirtualFile file) {
        super(editorName, file);
        setComponent(new WebpImagePanel(file));
    }

    @Override
    public void dispose() {
        super.dispose();
        ((WebpImagePanel) getComponent()).dispose();
    }

}
