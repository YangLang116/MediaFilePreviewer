package com.xtu.plugin.previewer.video;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.previewer.video.ui.VideoComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public class VideoFileEditor extends UserDataHolderBase implements FileEditor {

    private final VirtualFile videoFile;
    private final VideoComponent videoComponent;
    private final JPanel videoContainer;

    public VideoFileEditor(VirtualFile videoFile) {
        this.videoFile = videoFile;
        this.videoComponent = new VideoComponent();
        this.videoComponent.setMaxVideoSize(new Dimension(720, 480));
        this.videoContainer = new JPanel(new BorderLayout());
        this.videoContainer.add(videoComponent, BorderLayout.CENTER);
        this.videoComponent.setOnFrameChangeListener(videoContainer::setSize);
    }

    @Override
    public @NotNull JComponent getComponent() {
        this.videoComponent.playVideo(this.videoFile.getPath(), true);
        return this.videoContainer;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.videoContainer;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "Video Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return this.videoFile.isValid();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        this.videoComponent.release();
    }
}
