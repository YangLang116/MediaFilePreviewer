package com.xtu.plugin.previewer.svga.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.FileUtils;
import com.xtu.plugin.previewer.common.OnResultListener;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;


//SVGA Html生成器
@SuppressWarnings("SpellCheckingInspection")
public class SvgaHtmlGenerator {

    public static void generate(@NotNull VirtualFile svgaFile,
                                @NotNull OnResultListener listener) {
        ReadAction.run(() -> readAndGenerate(svgaFile, listener));
    }

    private static void readAndGenerate(@NotNull VirtualFile svgaFile,
                                        @NotNull OnResultListener listener) {
        try {
            String svgaContent = readSvgaContent(VfsUtil.loadBytes(svgaFile));
            String htmlContent = FileUtils.readTextFromResource("/html/svga/index.html");
            String cssContent = FileUtils.readTextFromResource("/html/svga/index.css");
            String jsZipMinContent = FileUtils.readTextFromResource("/html/svga/libs/jszip.min.js");
            String jsZipUtilsContent = FileUtils.readTextFromResource("/html/svga/libs/jszip-utils.min.js");
            String jsSvgaContent = FileUtils.readTextFromResource("/html/svga/libs/svga.min.js");
            String jsContent = FileUtils.readTextFromResource("/html/svga/index.js");
            //拼装html内容
            String result = htmlContent.replace("{style_placeholder}", cssContent)
                    .replace("{script_placeholder}",
                            jsZipMinContent + " " + jsZipUtilsContent + " " + jsSvgaContent + " " + jsContent)
                    .replace("{file_content_placeholder}", svgaContent);
            System.out.println(result);
            ApplicationManager.getApplication().invokeLater(() -> listener.onResult(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readSvgaContent(byte[] fileContent) {
        String base64Str = Base64.getEncoder().encodeToString(fileContent);
        return "data:application/octet-stream;base64," + base64Str;
    }
}
