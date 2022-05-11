package com.xtu.plugin.previewer.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static String readTextFromResource(@NotNull String name) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = FileUtils.class.getClassLoader().getResourceAsStream(name);
            outputStream = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        } finally {
            close(inputStream);
            close(outputStream);
        }
    }

    private static void close(@Nullable Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
