package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaFileEditorProvider implements FileEditorProvider, DumbAware {

    private static final String EDITOR_NAME = "SVGA Editor";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        String fileExtension = virtualFile.getExtension();
        if (fileExtension == null) return false;
        return StringUtils.equals(fileExtension.toLowerCase(Locale.ROOT), "svga");
    }

    @Override
    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new SvgaFileEditor(EDITOR_NAME, virtualFile);
    }

    @Override
    @NotNull
    @NonNls
    public String getEditorTypeId() {
        return EDITOR_NAME;
    }

    @Override
    @NotNull
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
