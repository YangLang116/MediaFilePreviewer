package com.xtu.plugin.previewer;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.utils.FileUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class MediaFileEditorProvider implements FileEditorProvider, DumbAware {

    private final String editorName;
    private final String supportExtension;

    public MediaFileEditorProvider(@NotNull String editorName, @NotNull String supportExtension) {
        this.editorName = editorName;
        this.supportExtension = supportExtension;
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return FileUtils.matchExtension(virtualFile, supportExtension);
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return createEditor(project, editorName, virtualFile);
    }

    public abstract FileEditor createEditor(@NotNull Project project,
                                            @NotNull String editorName,
                                            @NotNull VirtualFile virtualFile);

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return this.editorName;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
