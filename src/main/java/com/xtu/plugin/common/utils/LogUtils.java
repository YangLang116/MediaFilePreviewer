package com.xtu.plugin.common.utils;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {

    private static Logger getLogger() {
        return Logger.getInstance("MediaPreviewer -> ");
    }

    public static void info(@NotNull String message) {
        getLogger().info(message);
    }

    public static void error(@NotNull Exception exception) {
        String content = "message: " + exception.getMessage() + "\n" +
                "stackTrace: \n" + getStackTrace(exception);
        getLogger().error(content);
        AdviceUtils.submitData("error catch", content);
    }

    private static String getStackTrace(@NotNull Exception e) {
        StringWriter strWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(strWriter, true));
        return strWriter.toString();
    }

}
