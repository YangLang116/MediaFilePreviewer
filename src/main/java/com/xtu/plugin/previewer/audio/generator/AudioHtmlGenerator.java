package com.xtu.plugin.previewer.audio.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import com.xtu.plugin.previewer.common.FileUtils;
import com.xtu.plugin.previewer.common.OnResultListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


//Audio Html生成器
public class AudioHtmlGenerator {

    public static void generate(@NotNull VirtualFile audioFile,
                                @NotNull OnResultListener listener) {
        ReadAction.run(() -> readAndGenerate(audioFile, listener));
    }

    private static void readAndGenerate(@NotNull VirtualFile audioFile,
                                        @NotNull OnResultListener listener) {
        try {
            byte[] audioFileBytes = VfsUtil.loadBytes(audioFile);
            int[] audioFileData = new int[audioFileBytes.length];
            for (int i = 0; i < audioFileBytes.length; i++) {
                audioFileData[i] = audioFileBytes[i] & 0xff;
            }
            String dataStr = Arrays.toString(audioFileData);
            String htmlContent = FileUtils.readTextFromResource("/html/audio/index.html");
            final String result = htmlContent.replace("{title}", audioFile.getName())
                    .replace("{body_color}", UIUtil.isUnderDarcula() ? "black" : "white")
                    .replace("{data}", dataStr);
            ApplicationManager.getApplication().invokeLater(() -> listener.onResult(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
