package com.xtu.plugin.previewer.common;

import kotlin.Pair;

public class DisplayUtils {

    private static final int MAX_SIZE = 320;

    public static Pair<Integer, Integer> getFitSize(int width, int height) {
        if (width < MAX_SIZE && height < MAX_SIZE) return new Pair<>(width, height);
        if (height == 0) return new Pair<>(0, 0);
        int rate = width / height;
        return width > height ?
                new Pair<>(MAX_SIZE, MAX_SIZE / rate) : new Pair<>(MAX_SIZE * rate, MAX_SIZE);
    }

}
