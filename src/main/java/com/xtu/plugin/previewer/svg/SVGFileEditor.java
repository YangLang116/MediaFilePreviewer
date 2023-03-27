package com.xtu.plugin.previewer.svg;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class SVGFileEditor extends UserDataHolderBase implements FileEditor {

    private final String name;
    private final VirtualFile file;
    private final SVGImagePanel imagePanel;

    public SVGFileEditor(@NotNull String name, @NotNull VirtualFile file, @NotNull String fileExtension) {
        this.name = name;
        this.file = file;
        this.imagePanel = new SVGImagePanel(file, fileExtension);
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return imagePanel;
    }

    @NotNull
    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getName() {
        return name;
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
        return file.isValid();
    }

    @Nullable
    @Override
    public VirtualFile getFile() {
        return file;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    @Nullable
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        return imagePanel;
    }

    @Override
    public void dispose() {
        this.imagePanel.dispose();
    }
}
