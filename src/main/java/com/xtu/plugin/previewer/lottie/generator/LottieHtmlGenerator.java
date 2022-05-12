package com.xtu.plugin.previewer.lottie.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.DisplayUtils;
import com.xtu.plugin.previewer.common.FileUtils;
import com.xtu.plugin.previewer.common.OnResultListener;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


//Lottie Html生成器
public class LottieHtmlGenerator {

    public static void generate(@NotNull VirtualFile lottieFile,
                                @NotNull OnResultListener listener) {
        ReadAction.run(() -> readAndGenerate(lottieFile, listener));
    }

    private static void readAndGenerate(@NotNull VirtualFile lottieFile,
                                        @NotNull OnResultListener listener) {
        try {
            String lottieContent = VfsUtil.loadText(lottieFile);
            JSONObject lottieJson = new JSONObject(lottieContent);
            int width = (int) lottieJson.optDouble("w", 0);
            int height = (int) lottieJson.optDouble("h", 0);
            Pair<Integer, Integer> fitSize = DisplayUtils.getFitSize(width, height);
            String htmlContent = FileUtils.readTextFromResource("/html/lottie/index.html");
            String cssContent = FileUtils.readTextFromResource("/html/lottie/index.css");
            String lottieJsContent = FileUtils.readTextFromResource("/html/lottie/libs/lottie_light.min.js");
            String jsContent = FileUtils.readTextFromResource("/html/lottie/index.js");
            //拼装html内容
            String result = htmlContent.replace("{style_placeholder}", cssContent)
                    .replace("{script_placeholder}", lottieJsContent + " " + jsContent)
                    .replace("{lottie_width}", String.valueOf(fitSize.getFirst() + 20))
                    .replace("{lottie_height}", String.valueOf(fitSize.getSecond() + 20))
                    .replace("{data_placeholder}", lottieContent);
            ApplicationManager.getApplication().invokeLater(() -> listener.onResult(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
