package com.xtu.plugin.common.utils;

import org.jetbrains.annotations.Nullable;

public class StringUtils {

    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.trim().isEmpty();
    }
}
