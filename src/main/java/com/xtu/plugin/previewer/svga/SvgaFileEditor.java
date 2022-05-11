package com.xtu.plugin.previewer.svga;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import com.xtu.plugin.previewer.svga.generator.SvgaHtmlGenerator;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaFileEditor extends UserDataHolderBase implements FileEditor {

    private static final String NAME = "SVGA File Editor";

    private final VirtualFile virtualFile;

    public SvgaFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        this.virtualFile = file;
    }

    @Override
    @NotNull
    public JComponent getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        JBCefBrowser browser = new JBCefBrowser();
        rootPanel.add(browser.getComponent(), BorderLayout.CENTER);
        SvgaHtmlGenerator.generate(this.virtualFile, browser::loadHTML);
        return rootPanel;
    }

    @Override
    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    public String getName() {
        return NAME;
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
        return this.virtualFile.isValid();
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
    public void dispose() {
    }
}
