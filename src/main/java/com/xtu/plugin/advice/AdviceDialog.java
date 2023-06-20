package com.xtu.plugin.advice;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.util.ui.JBUI;
import com.xtu.plugin.common.utils.StreamUtils;
import com.xtu.plugin.common.utils.ToastUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdviceDialog extends DialogWrapper {

    private static final String APP_KEY = "MediaFilePreviewer";
    private static final String sURL = "https://iflutter.toolu.cn/api/advice";

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
        submitData(title, content);
    }

    private static void submitData(String title, String content) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("app_key", APP_KEY);
        jsonData.put("title", title);
        jsonData.put("content", content);
        String dataStr = jsonData.toString();
        try {
            URL url = new URL(sURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(5 * 1000);
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setDoOutput(true);
            StreamUtils.writeToStream(urlConnection.getOutputStream(), dataStr);
            urlConnection.getResponseCode();
            ToastUtil.make(MessageType.INFO, "thank you for submitting ~");
        } catch (IOException e) {
            ToastUtil.make(MessageType.ERROR, e.getMessage());
        }
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

    @Override
    protected @Nullable
    @NonNls String getDimensionServiceKey() {
        return getClass().getSimpleName();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.titleField;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.rootPanel;
    }

    private String getAdviceTitle() {
        return this.titleField.getText();
    }

    private String getAdviceContent() {
        return this.contentField.getText();
    }
}
