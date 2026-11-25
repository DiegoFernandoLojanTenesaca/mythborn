package com.lautish.mythborn.init;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.mythborn.util.LuckyStyle;

public class ModConfig extends TXFConfig {
    @Entry(min = 0, max = 100000)
    public static int luckyChance = 200;
    @Entry(min = 0, max = 100000)
    public static int alphaChance = 1000;
    @Entry(min = 0, max = 100000)
    public static int titanChance = 4000;
    @Entry(syncServer = true)
    public static LuckyStyle luckyStyle = LuckyStyle.GOLDEN;
    @Entry(syncServer = true)
    public static LuckyStyle alphaStyle = LuckyStyle.SOUL;
    @Entry(syncServer = true)
    public static LuckyStyle titanStyle = LuckyStyle.EMBERS;
    @Entry
    public static boolean aggressiveAnimals = true;
    @Entry
    public static boolean bossBar = true;
    @Entry
    public static boolean veil = true;
}
