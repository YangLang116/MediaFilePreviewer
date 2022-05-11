package com.xtu.plugin.previewer.lottie;

import com.intellij.lang.Language;

public class LottieLanguage extends Language {

    public static LottieLanguage INSTANCE = new LottieLanguage();

    private LottieLanguage() {
        super("lottie");
    }
}
