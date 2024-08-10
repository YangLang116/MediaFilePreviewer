package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.MediaFileEditor;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditor extends MediaFileEditor {

    public SVGFileEditor(@NotNull String editorName, @NotNull VirtualFile file) {
        super(editorName, file);
        setComponent(new SVGImagePanel(file));
    }

    @Override
    public void dispose() {
        super.dispose();
        ((SVGImagePanel) getComponent()).dispose();
    }
}
