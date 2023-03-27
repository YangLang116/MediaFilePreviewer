package com.xtu.plugin.configuration;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class SettingsConfiguration implements SearchableConfigurable {

    private static final String KEY_AUTO_PLAY = "mp_play";

    private JPanel rootPanel;
    private JCheckBox isAutoPlayBox;

    @NotNull
    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public String getDisplayName() {
        return "MediaFilePreviewer";
    }


    @Override
    public JComponent createComponent() {
        return rootPanel;
    }

    @Override
    public void reset() {
        this.isAutoPlayBox.setSelected(isAutoPlay());
    }

    @Override
    public boolean isModified() {
        return isAutoPlayBox.isSelected() != isAutoPlay();
    }

    @Override
    public void apply() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue(KEY_AUTO_PLAY, isAutoPlayBox.isSelected());
    }

    public static boolean isAutoPlay() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getBoolean(KEY_AUTO_PLAY, true);
    }
}
