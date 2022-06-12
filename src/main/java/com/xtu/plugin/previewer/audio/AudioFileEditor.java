package com.xtu.plugin.previewer.audio;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.audio.generator.AudioHtmlGenerator;
import com.xtu.plugin.previewer.common.BaseFileEditor;
import org.jetbrains.annotations.NotNull;

public class AudioFileEditor extends BaseFileEditor {
    public AudioFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        super("Audio Editor", project, file);
        AudioHtmlGenerator.generate(file, this::loadHtml);
    }
}
