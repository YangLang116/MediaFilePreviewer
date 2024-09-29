package com.xtu.plugin.previewer.lottie;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.HtmLoadEditor;
import com.xtu.plugin.previewer.HtmlGenerator;
import com.xtu.plugin.previewer.lottie.generator.LottieHtmlGenerator;
import org.jetbrains.annotations.NotNull;

public class LottieHtmLoadEditor extends HtmLoadEditor {

    public LottieHtmLoadEditor(@NotNull Project project,
                               @NotNull String editorName,
                               @NotNull VirtualFile file) {
        super(project, editorName, file);
    }

    @Override
    public void load(@NotNull Project project, @NotNull VirtualFile file) {
        LottieHtmlGenerator htmlGenerator = new LottieHtmlGenerator(project, file, new HtmlGenerator.OnHTMLGenerateListener() {
            @Override
            public void onReady(@NotNull String htmlContent) {
                setHtml(htmlContent);
            }

            @Override
            public void onFail() {
                showErrorTip("Fail to load Lottie file");
            }
        });
        htmlGenerator.generate();
    }
}
