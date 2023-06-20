package com.xtu.plugin.configuration;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.SearchableConfigurable;
import com.xtu.plugin.advice.AdviceDialog;
import com.xtu.plugin.common.utils.WindowUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class SettingsConfiguration implements SearchableConfigurable {

    private static final String KEY_AUTO_PLAY = "mp_auto_play";

    private JPanel rootPanel;
    private JCheckBox isAutoPlayBox;
    private JLabel adviceLabel;

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
        this.adviceLabel.setText("<html><u>建议与反馈</u></html>");
        this.adviceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JComponent rootPanel = WindowUtils.getVisibleRootPanel();
                AdviceDialog.show(rootPanel);
            }
        });
    }

    @Override
    public boolean isModified() {
        return isAutoPlayBox.isSelected() != isAutoPlay();
    }

    @Override
    public void apply() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        propertiesComponent.setValue(KEY_AUTO_PLAY, isAutoPlayBox.isSelected(), true);
    }

    public static boolean isAutoPlay() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        return propertiesComponent.getBoolean(KEY_AUTO_PLAY, true);
    }
}
