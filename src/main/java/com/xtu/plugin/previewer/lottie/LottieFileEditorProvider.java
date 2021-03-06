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

public class LottieFileEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (!(virtualFile.getFileType() instanceof LottieFileType)) return false;
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
        return new LottieFileEditor(project, virtualFile);
    }

    @Override
    @NotNull
    @NonNls
    public String getEditorTypeId() {
        return "Lottie Editor";
    }

    @Override
    @NotNull
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
