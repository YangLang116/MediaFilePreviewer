package com.xtu.plugin.common.utils;

import kotlin.Pair;

public class DisplayUtils {

    private static final int MAX_SIZE = 320;

    public static Pair<Integer, Integer> getFitSize(int width, int height) {
        if (width < MAX_SIZE && height < MAX_SIZE) return new Pair<>(width, height);
        if (height == 0) return new Pair<>(0, 0);
        double rate = width * 1.0f / height;
        if (width > height) return new Pair<>(MAX_SIZE, (int) (MAX_SIZE / rate));
        return new Pair<>((int) (MAX_SIZE * rate), MAX_SIZE);
    }

}
