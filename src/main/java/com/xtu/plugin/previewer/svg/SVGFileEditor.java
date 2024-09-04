package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.MediaFileEditor;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditor extends MediaFileEditor<SVGInfoPanel> {

    public SVGFileEditor(@NotNull String editorName, @NotNull VirtualFile file) {
        super(editorName, file);
        setComponent(new SVGInfoPanel(file));
    }

    @Override
    public void dispose() {
        super.dispose();
        getComponent().dispose();
    }
}
