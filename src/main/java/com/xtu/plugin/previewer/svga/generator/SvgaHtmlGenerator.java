package com.xtu.plugin.previewer.svga.generator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Formats;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.xtu.plugin.common.utils.ColorUtils;
import com.xtu.plugin.common.utils.FileUtils;
import com.xtu.plugin.previewer.HtmlGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;


//SVGA Html生成器
@SuppressWarnings("SpellCheckingInspection")
public class SvgaHtmlGenerator extends HtmlGenerator {

    public SvgaHtmlGenerator(@NotNull Project project,
                             @NotNull VirtualFile file,
                             @NotNull HtmlGenerator.OnHTMLGenerateListener readyListener) {
        super(project, file, readyListener);
    }

    @Override
    public @Nullable String createHtml() {
        try {
            VirtualFile animFile = getFile();
            byte[] fileContent = VfsUtil.loadBytes(animFile);
            String svgaContent = readSvgaContent(fileContent);
            String fileSize = Formats.formatFileSize(animFile.getLength()).toUpperCase();
            String htmlContent = FileUtils.loadTextFromResource("html/svga/index.html");
            String cssContent = FileUtils.loadTextFromResource("html/svga/index.css");
            String jsZipMinContent = FileUtils.loadTextFromResource("html/svga/libs/jszip.min.js");
            String jsZipUtilsContent = FileUtils.loadTextFromResource("html/svga/libs/jszip-utils.min.js");
            String jsSvgaContent = FileUtils.loadTextFromResource("html/svga/libs/svga.min.js");
            String jsContent = FileUtils.loadTextFromResource("html/svga/index.js");

            return htmlContent.replace("{style_placeholder}", cssContent)
                    .replace("{script_placeholder}", jsZipMinContent + " " + jsZipUtilsContent + " " + jsSvgaContent + " " + jsContent)
                    .replace("{body_color}", ColorUtils.toString(JBColor.background()))
                    .replace("{main_color}", ColorUtils.toString(JBColor.foreground()))
                    .replace("{file_content_placeholder}", svgaContent)
                    .replace("{file_Size}", fileSize);
        } catch (Exception e) {
            return null;
        }
    }

    private static String readSvgaContent(byte[] fileContent) {
        String base64Str = Base64.getEncoder().encodeToString(fileContent);
        return "data:application/octet-stream;base64," + base64Str;
    }
}
