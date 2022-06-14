package com.xtu.plugin.previewer.video.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import com.xtu.plugin.previewer.common.FileUtils;
import com.xtu.plugin.previewer.common.OnResultListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


//Video Html生成器
public class VideoHtmlGenerator {

    public static void generate(@NotNull VirtualFile videoFile,
                                @NotNull OnResultListener listener) {
        ReadAction.run(() -> readAndGenerate(videoFile, listener));
    }

    private static void readAndGenerate(@NotNull VirtualFile videoFile,
                                        @NotNull OnResultListener listener) {
        try {
            byte[] videoFileBytes = VfsUtil.loadBytes(videoFile);
            int[] videoFileData = new int[videoFileBytes.length];
            for (int i = 0; i < videoFileBytes.length; i++) {
                videoFileData[i] = videoFileBytes[i] & 0xff;
            }
            String dataStr = Arrays.toString(videoFileData);
            String htmlContent = FileUtils.readTextFromResource("/html/video/index.html");
            final String result = htmlContent.replace("{title}", videoFile.getName())
                    .replace("{lib_style}", FileUtils.readTextFromResource("/html/video/libs/video-js.css"))
                    .replace("{app_style}", FileUtils.readTextFromResource("/html/video/index.css"))
                    .replace("{lib_script}", FileUtils.readTextFromResource("/html/video/libs/video.min.js"))
                    .replace("{app_script}", FileUtils.readTextFromResource("/html/video/index.js"))
                    .replace("{body_color}", UIUtil.isUnderDarcula() ? "black" : "white")
                    .replace("{data}", dataStr);
            ApplicationManager.getApplication().invokeLater(() -> listener.onResult(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
