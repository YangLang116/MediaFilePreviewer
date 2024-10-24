package com.xtu.plugin.common.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("HttpUrlsUsage")
public class AdviceUtils {

    private static final String APP_KEY = "MediaFilePreviewer";

    private static final String sURL = "http://iflutter.toolu.cn/api/advice";

    public static void submitData(@NotNull String title, @NotNull String content) {
        ApplicationInfoEx appInfo = ApplicationInfoEx.getInstanceEx();
        JSONObject jsonData = new JSONObject();
        jsonData.put("title", title);
        jsonData.put("content", content);
        jsonData.put("app_key", APP_KEY);
        jsonData.put("version", VersionUtils.getPluginVersion());
        jsonData.put("os", SystemInfo.getOsNameAndVersion());
        jsonData.put("ide", appInfo.getFullApplicationName());
        jsonData.put("build", appInfo.getBuild().asString());
        String info = jsonData.toString();
        ApplicationManager.getApplication().executeOnPooledThread(() -> postData(info));
    }

    private static void postData(@NotNull String info) {
        try {
            URL url = new URL(sURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(5 * 1000);
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setDoOutput(true);
            StreamUtils.writeToStream(urlConnection.getOutputStream(), info);
            urlConnection.getResponseCode();
        } catch (IOException e) {
            //ignore
        }
    }
}
