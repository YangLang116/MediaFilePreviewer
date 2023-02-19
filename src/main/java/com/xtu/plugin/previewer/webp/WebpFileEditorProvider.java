package com.xtu.plugin.previewer.webp;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.utils.ImageUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class WebpFileEditorProvider implements FileEditorProvider {

    private static final String EXTENSION = "webp";
    private static final String EDITOR_NAME = "Webp Editor";


    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        String extension = virtualFile.getExtension();
        if (extension == null) return false;
        if (!StringUtils.equals(extension.toLowerCase(Locale.ROOT), EXTENSION)) return false;
        return ImageUtils.checkSupport(EXTENSION);
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new WebpFileEditor(EDITOR_NAME, virtualFile);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return EDITOR_NAME;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
