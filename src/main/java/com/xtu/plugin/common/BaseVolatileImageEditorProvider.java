package com.xtu.plugin.common;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.utils.ImageUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class BaseVolatileImageEditorProvider implements FileEditorProvider {

    public abstract String getEditorName();

    public abstract String getSupportExtension();

    public abstract FileEditor getEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String imageType);

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        String extension = virtualFile.getExtension();
        if (extension == null) return false;
        String supportExtension = getSupportExtension();
        if (!StringUtils.equals(extension.toLowerCase(Locale.ROOT), supportExtension)) return false;
        return ImageUtils.checkSupport(supportExtension);
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return getEditor(getEditorName(), virtualFile, getSupportExtension());
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return getEditorName();
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
