package com.xtu.plugin.common.image;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageCache<T extends Image> {

    private static final int MAX_CACHE_SIZE = 5;

    private final Map<Float, T> cache;

    public ImageCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    @Nullable
    public T get(float key) {
        return this.cache.get(key);
    }

    @Nullable
    public T find(float key) {
        T image = this.get(key);
        if (image != null) return image;
        // 从缓存获取当前比例图像，找不到则找最接近的比例
        float closest = this.cache.keySet().stream()
                .min((s1, s2) -> Float.compare(Math.abs(s1 - key), Math.abs(s2 - key)))
                .orElse(-1f);
        return closest > 0 ? this.get(closest) : null;
    }

    public void put(float key, T image) {
        this.cache.put(key, image);
        if (this.cache.size() <= MAX_CACHE_SIZE) return;
        // 移除最早的缓存
        float oldestScale = this.cache.keySet().stream()
                .filter(s -> s != key)
                .max((s1, s2) -> Float.compare(Math.abs(s1 - key), Math.abs(s2 - key)))
                .orElse(-1f);
        if (oldestScale > 0) {
            Image removed = this.cache.remove(oldestScale);
            if (removed != null) removed.flush();
        }
    }

    public void clear() {
        for (T img : this.cache.values()) {
            if (img != null) img.flush();
        }
        this.cache.clear();
    }
}
