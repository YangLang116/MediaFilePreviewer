package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.MediaFileEditorProvider;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaFileEditorProvider extends MediaFileEditorProvider {

    public SvgaFileEditorProvider() {
        super("SVGA Editor", "svga");
    }

    @Override
    public FileEditor createEditor(@NotNull Project project,
                                   @NotNull String editorName,
                                   @NotNull VirtualFile virtualFile) {
        return new SvgaHtmlLoadEditor(project, editorName, virtualFile);
    }
}
