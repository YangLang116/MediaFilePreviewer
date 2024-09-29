package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.HtmLoadEditor;
import com.xtu.plugin.previewer.HtmlGenerator;
import com.xtu.plugin.previewer.svga.generator.SvgaHtmlGenerator;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaHtmLoadEditor extends HtmLoadEditor {

    public SvgaHtmLoadEditor(@NotNull Project project,
                             @NotNull String editorName,
                             @NotNull VirtualFile file) {
        super(project, editorName, file);
    }

    @Override
    public void load(@NotNull Project project, @NotNull VirtualFile file) {
        SvgaHtmlGenerator htmlGenerator = new SvgaHtmlGenerator(project, file, new HtmlGenerator.OnHTMLGenerateListener() {
            @Override
            public void onReady(@NotNull String htmlContent) {
                setHtml(htmlContent);
            }

            @Override
            public void onFail() {
                showErrorTip("Fail to load SVGA file");
            }
        });
        htmlGenerator.generate();
    }
}
