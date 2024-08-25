package com.xtu.plugin.common.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static String loadTextFromResource(@NotNull String resPath) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = FileUtils.class.getClassLoader().getResourceAsStream(resPath);
            if (inputStream == null) return "";
            outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        } finally {
            CloseUtils.close(inputStream);
            CloseUtils.close(outputStream);
        }
    }

    public static boolean matchExtension(@NotNull VirtualFile file, @NotNull String extension) {
        String fileExtension = file.getExtension();
        if (fileExtension == null) return false;
        return fileExtension.equalsIgnoreCase(extension);
    }

    public static boolean isLottieFile(@NotNull VirtualFile file) {
        try {
            String jsonContent = VfsUtil.loadText(file);
            if (StringUtils.isEmpty(jsonContent)) return false;
            JSONObject jsonObject = new JSONObject(jsonContent);
            return jsonObject.has("v")
                    && jsonObject.has("w")
                    && jsonObject.has("h");
        } catch (Exception e) {
            return false;
        }
    }

    @TestOnly
    public static void dumpHtml(@NotNull Project project, @NotNull String htmlContent) {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> WriteAction.run(() -> {
            try {
                VirtualFile projectDir = ProjectUtil.guessProjectDir(project);
                if (projectDir == null) return;
                String fileName = "dump.html";
                VirtualFile dumpFile = projectDir.findChild(fileName);
                if (dumpFile == null) {
                    dumpFile = projectDir.createChildData(project, fileName);
                }
                VfsUtil.saveText(dumpFile, htmlContent);
            } catch (IOException e) {
                //ignore
            }
        }));
    }
}
