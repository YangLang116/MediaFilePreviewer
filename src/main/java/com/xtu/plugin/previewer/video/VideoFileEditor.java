package com.xtu.plugin.previewer.video;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.common.BaseFileEditor;
import com.xtu.plugin.previewer.video.generator.VideoHtmlGenerator;

public class VideoFileEditor extends BaseFileEditor {

    public VideoFileEditor(Project project, VirtualFile videoFile) {
        super("Video Editor", project, videoFile);
        VideoHtmlGenerator.generate(videoFile, this::loadHtml);
    }
}
