package com.xtu.plugin.previewer.video;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.video.mp4.VideoMp4FileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class VideoFileEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        FileType fileType = virtualFile.getFileType();
        return fileType instanceof VideoMp4FileType;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new VideoFileEditor(project, virtualFile);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return "Video Editor Provider";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
