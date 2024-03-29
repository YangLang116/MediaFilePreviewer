package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Locale;

public class LottieFileEditorProvider implements FileEditorProvider, DumbAware {

    private static final String EDITOR_NAME = "Lottie Editor";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        String fileExtension = virtualFile.getExtension();
        if (fileExtension == null) return false;
        if (!StringUtils.equals(fileExtension.toLowerCase(Locale.ROOT), "json")) return false;
        try {
            String jsonContent = VfsUtil.loadText(virtualFile);
            if (StringUtils.isEmpty(jsonContent)) return false;
            JSONObject jsonObject = new JSONObject(jsonContent);
            return jsonObject.has("v")
                    && jsonObject.has("w")
                    && jsonObject.has("h");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new LottieHtmLoadEditor(EDITOR_NAME, virtualFile);
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
