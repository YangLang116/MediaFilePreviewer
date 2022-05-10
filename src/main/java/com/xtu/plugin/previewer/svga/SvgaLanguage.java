package com.xtu.plugin.previewer.svga;

import com.intellij.lang.Language;

@SuppressWarnings("SpellCheckingInspection")
public class SvgaLanguage extends Language {

    public static final SvgaLanguage INSTANCE = new SvgaLanguage();

    private SvgaLanguage() {
        super("svga");
    }
}
