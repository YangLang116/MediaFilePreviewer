package com.xtu.plugin.configuration;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.Objects;

public final class SettingsConfiguration implements SearchableConfigurable {

    private static final String KEY_AUTO_PLAY = "mp_auto_play";
    private static final String KEY_PLAY_SPEED = "mp_play_speed";

    private JPanel rootPanel;
    private JCheckBox isAutoPlayBox;
    private JTextField playSpeed;

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
        playSpeed.setDocument(new NumberDocument());
        return rootPanel;
    }

    @Override
    public void reset() {
        this.isAutoPlayBox.setSelected(isAutoPlay());
        this.playSpeed.setText(getPlaySpeed());
    }

    @Override
    public boolean isModified() {
        return isAutoPlayBox.isSelected() != isAutoPlay() || !Objects.equals(playSpeed.getText(), getPlaySpeed());
    }

    @Override
    public void apply() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue(KEY_AUTO_PLAY, isAutoPlayBox.isSelected());
        propertiesComponent.setValue(KEY_PLAY_SPEED, playSpeed.getText());
    }

    public static boolean isAutoPlay() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getBoolean(KEY_AUTO_PLAY, false);
    }

    public static String getPlaySpeed() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getValue(KEY_PLAY_SPEED, "50");
    }

    private static class NumberDocument extends PlainDocument {

        public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
            if (this.isNumeric(var2)) {
                super.insertString(var1, var2, var3);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private boolean isNumeric(String var1) {
            try {
                Long.valueOf(var1);
                return true;
            } catch (NumberFormatException var3) {
                return false;
            }
        }
    }
}
