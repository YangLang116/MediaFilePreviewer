package com.xtu.plugin.previewer.video;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;

public class VideoFileEditor extends UserDataHolderBase implements FileEditor {

    private final Project project;
    private final VirtualFile videoFile;

    public VideoFileEditor(Project project, VirtualFile videoFile) {
        this.project = project;
        this.videoFile = videoFile;
    }

    @Override
    public @NotNull JComponent getComponent() {
        final JFXPanel VFXPanel = new JFXPanel();

        File video_source = new File(this.videoFile.getPath());
        Media m = new Media(video_source.toURI().toString());
        MediaPlayer player = new MediaPlayer(m);
        MediaView viewer = new MediaView(player);

        StackPane root = new StackPane();
        Scene scene = new Scene(root);
//        javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();

//        viewer.setX((screen.getWidth() - videoPanel.getWidth()) / 2);

//        viewer.setY((screen.getHeight() - videoPanel.getHeight()) / 2);
        DoubleProperty width = viewer.fitWidthProperty();

        DoubleProperty height = viewer.fitHeightProperty();

        width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));

        height.bind(Bindings.selectDouble(viewer.sceneProperty(), "height"));

        viewer.setPreserveRatio(true);

// add video to stackpane

        root.getChildren().add(viewer);

        VFXPanel.setScene(scene);

//player.play();

        return VFXPanel;

    }

    @Override
    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return null;
    }


    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    @Override
    public String getName() {
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
    }
}
