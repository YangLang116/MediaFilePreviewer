package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.MediaFileEditorProvider;
import org.jetbrains.annotations.NotNull;

public class WebpFileEditorProvider extends MediaFileEditorProvider {

    public WebpFileEditorProvider() {
        super("Webp Editor", "webp");
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project,
                                            @NotNull String editorName,
                                            @NotNull VirtualFile virtualFile) {
        return new WebpFileEditor(editorName, virtualFile);
    }
}
