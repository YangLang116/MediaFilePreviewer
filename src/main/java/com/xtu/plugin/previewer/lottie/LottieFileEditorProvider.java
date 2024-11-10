package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.utils.FileUtils;
import com.xtu.plugin.previewer.MediaFileEditorProvider;
import org.jetbrains.annotations.NotNull;

public class LottieFileEditorProvider extends MediaFileEditorProvider {

    public LottieFileEditorProvider() {
        super("Lottie Editor", "json");
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        boolean matchExtension = super.accept(project, virtualFile);
        if (!matchExtension) return false;
        return FileUtils.isLottieFile(virtualFile);
    }

    @Override
    @NotNull
    public FileEditor createEditor(@NotNull Project project,
                                   @NotNull String editorName,
                                   @NotNull VirtualFile virtualFile) {
        return new LottieHtmlLoadEditor(project, editorName, virtualFile);
    }
}
