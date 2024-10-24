package com.xtu.plugin.advice;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.util.ui.JBUI;
import com.xtu.plugin.common.utils.AdviceUtils;
import com.xtu.plugin.common.utils.StringUtils;
import com.xtu.plugin.common.utils.ToastUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AdviceDialog extends DialogWrapper {

    private JPanel rootPanel;
    private JLabel titleLabel;
    private JTextField titleField;
    private JLabel contentLabel;
    private JTextArea contentField;

    public static void show(JComponent parentComponent) {
        AdviceDialog dialog = new AdviceDialog(parentComponent);
        boolean isOk = dialog.showAndGet();
        if (!isOk) return;
        String title = dialog.getAdviceTitle();
        String content = dialog.getAdviceContent();
        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(content)) {
            ToastUtil.make(MessageType.ERROR, "Title or Content is Empty ~");
            return;
        }
        AdviceUtils.submitData(title.trim(), content.trim());
        ToastUtil.make(MessageType.INFO, "thank you for submitting ~");
    }

    private AdviceDialog(JComponent parentComponent) {
        super(null, parentComponent, false, IdeModalityType.PROJECT);
        setTitle("Suggestion & Feedback");
        initUI();
        init();
    }

    private void initUI() {
        this.rootPanel.setBorder(JBUI.Borders.empty(5));
        this.titleLabel.setBorder(JBUI.Borders.emptyBottom(3));
        this.contentLabel.setBorder(JBUI.Borders.empty(5, 0, 3, 0));
        this.contentField.setBorder(JBUI.Borders.empty(3, 5));
    }

    @Nullable
    @NonNls
    @Override
    protected String getDimensionServiceKey() {
        return getClass().getSimpleName();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.titleField;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return this.rootPanel;
    }

    @Nullable
    private String getAdviceTitle() {
        return this.titleField.getText();
    }

    @Nullable
    private String getAdviceContent() {
        return this.contentField.getText();
    }
}
