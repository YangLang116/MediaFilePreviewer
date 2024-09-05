package com.xtu.plugin.decorator;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.common.utils.FileUtils;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MediaFileDecorator implements ProjectViewNodeDecorator {
    @Override
    public void decorate(ProjectViewNode<?> node, PresentationData data) {
        VirtualFile file = node.getVirtualFile();
        if (file == null || file.isDirectory()) return;
        String extension = file.getExtension();
        if (extension == null) return;
        switch (extension.toLowerCase()) {
            case "svg":
                setIcon(node, PluginIcons.SVG);
                break;
            case "svga":
                setIcon(node, PluginIcons.SVGA);
                break;
            case "webp":
                setIcon(node, PluginIcons.WEBP);
                break;
            case "json":
                if (FileUtils.isLottieFile(file)) {
                    setIcon(node, PluginIcons.LOTTIE);
                }
                break;
            default:
                break;
        }
    }

    private void setIcon(@NotNull ProjectViewNode<?> node, @NotNull Icon icon) {
        PresentationData presentation = node.getPresentation();
        presentation.setIcon(icon);
    }
}
