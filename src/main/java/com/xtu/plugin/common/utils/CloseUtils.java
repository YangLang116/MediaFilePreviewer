package com.xtu.plugin.common.utils;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {

    public static void close(@Nullable Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            //ignore
        }
    }
}
