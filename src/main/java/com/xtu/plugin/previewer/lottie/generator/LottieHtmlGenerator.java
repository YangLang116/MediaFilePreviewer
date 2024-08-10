package com.xtu.plugin.previewer.lottie.generator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.xtu.plugin.common.utils.ColorUtils;
import com.xtu.plugin.common.utils.FileUtils;
import com.xtu.plugin.previewer.HtmlGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


//Lottie Html生成器
public class LottieHtmlGenerator extends HtmlGenerator {

    public LottieHtmlGenerator(@NotNull Project project,
                               @NotNull VirtualFile file,
                               @NotNull HtmlGenerator.OnHTMLGenerateListener readyListener) {
        super(project, file, readyListener);
    }

    @Override
    public @Nullable String createHtml() {
        try {
            String lottieContent = VfsUtil.loadText(getFile());
            String htmlContent = FileUtils.loadTextFromResource("html/lottie/index.html");
            String cssContent = FileUtils.loadTextFromResource("html/lottie/index.css");
            String lottieJsContent = FileUtils.loadTextFromResource("html/lottie/libs/lottie_light.min.js");
            String jsContent = FileUtils.loadTextFromResource("html/lottie/index.js");

            return htmlContent.replace("{style_placeholder}", cssContent)
                    .replace("{script_placeholder}", lottieJsContent + " " + jsContent)
                    .replace("{body_color}", ColorUtils.toString(JBColor.background()))
                    .replace("{main_color}", ColorUtils.toString(JBColor.foreground()))
                    .replace("{file_content_placeholder}", lottieContent);
        } catch (Exception e) {
            return null;
        }
    }
}
