package com.xtu.plugin.previewer.common;

import org.jetbrains.annotations.NotNull;

public interface OnResultListener {

    void onResult(@NotNull String htmlContent);
}