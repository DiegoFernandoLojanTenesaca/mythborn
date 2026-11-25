package com.lautish.alphamobs.util;

public enum LuckyStyle {
    GOLDEN(0xFFC83C),
    STARRY(0xFFFFFF),
    SOUL(0x40C8FF),
    EMBERS(0xFF3020),
    TOTEM(0x60FF60);

    private final int color;

    LuckyStyle(int color) {
        this.color = color;
    }

    public int color() {
        return color;
    }
}
