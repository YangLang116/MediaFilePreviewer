package com.xtu.plugin.previewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class HtmlGenerator {

    @SuppressWarnings("FieldCanBeLocal")
    private final Project project;
    private final VirtualFile file;
    private final OnHTMLGenerateListener readyListener;

    public HtmlGenerator(@NotNull Project project,
                         @NotNull VirtualFile file,
                         @NotNull HtmlGenerator.OnHTMLGenerateListener readyListener) {
        this.project = project;
        this.file = file;
        this.readyListener = readyListener;
    }

    public void generate() {
        String html = createHtml();
        if (StringUtils.isEmpty(html)) {
            readyListener.onFail();
        } else {
//            FileUtils.dumpHtml(project, html);  //testOnly
            readyListener.onReady(html);
        }
    }

    @NotNull
    public VirtualFile getFile() {
        return file;
    }

    @Nullable
    public abstract String createHtml();

    public interface OnHTMLGenerateListener {

        void onReady(@NotNull String htmlContent);

        void onFail();
    }
}
