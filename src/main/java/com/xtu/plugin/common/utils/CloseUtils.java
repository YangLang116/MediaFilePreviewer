package com.xtu.plugin.common.utils;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
