package com.xtu.plugin.previewer;

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

public abstract class MediaFileEditor extends UserDataHolderBase implements FileEditor {

    private final String editorName;
    private final VirtualFile file;
    private JComponent rootComponent;

    public MediaFileEditor(@NotNull String editorName, @NotNull VirtualFile file) {
        this.editorName = editorName;
        this.file = file;
    }

    public void setComponent(@NotNull JComponent rootComponent) {
        this.rootComponent = rootComponent;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return rootComponent;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return rootComponent;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return editorName;
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public VirtualFile getFile() {
        return file;
    }

    @Override
    public boolean isValid() {
        return file.isValid();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void dispose() {
    }
}
