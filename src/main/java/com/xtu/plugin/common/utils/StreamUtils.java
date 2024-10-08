package com.xtu.plugin.common.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StreamUtils {

    public static void writeToStream(@NotNull OutputStream outputStream, @NotNull String content) throws IOException {
        try {
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } finally {
            CloseUtils.close(outputStream);
        }
    }
}
