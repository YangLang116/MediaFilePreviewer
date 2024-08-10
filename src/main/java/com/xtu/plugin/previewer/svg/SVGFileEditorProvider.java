package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.MediaFileEditorProvider;
import org.jetbrains.annotations.NotNull;

public class SVGFileEditorProvider extends MediaFileEditorProvider {

    public SVGFileEditorProvider() {
        super("SVG Editor", "svg");
    }

    @Override
    public FileEditor createEditor(@NotNull Project project,
                                   @NotNull String editorName,
                                   @NotNull VirtualFile virtualFile) {
        return new SVGFileEditor(editorName, virtualFile);
    }
}
