package com.xtu.plugin.configuration;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.options.SearchableConfigurable;
import com.xtu.plugin.advice.AdviceDialog;
import com.xtu.plugin.common.utils.PluginUtils;
import com.xtu.plugin.common.utils.WindowUtils;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class SettingsConfiguration implements SearchableConfigurable {

    private static final String sGithubUrl = "https://github.com/YangLang116/MediaFilePreviewer";
    private static final String sPluginUrl = "https://plugins.jetbrains.com/plugin/19138-mediafilepreviewer";

    private JPanel rootPanel;
    private JCheckBox isAutoPlayBox;
    private JLabel githubLabel;
    private JLabel starLabel;
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
        this.isAutoPlayBox.setSelected(PluginUtils.isAutoPlay());

        githubLabel.setIcon(PluginIcons.GITHUB);
        githubLabel.setText("<html><u>Source Code</u></html>");
        githubLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        githubLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.open(sGithubUrl);
            }
        });
        starLabel.setIcon(PluginIcons.STAR);
        starLabel.setText("<html><u>Star Plugin</u></html>");
        starLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        starLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.open(sPluginUrl);
            }
        });
        adviceLabel.setIcon(PluginIcons.NOTE);
        adviceLabel.setText("<html><u>Suggestion & Feedback</u></html>");
        adviceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JComponent rootPanel = WindowUtils.getVisibleRootPanel();
                AdviceDialog.show(rootPanel);
            }
        });
    }

    @Override
    public boolean isModified() {
        return isAutoPlayBox.isSelected() != PluginUtils.isAutoPlay();
    }

    @Override
    public void apply() {
        PluginUtils.setAutoPlay(isAutoPlayBox.isSelected());
    }
}
