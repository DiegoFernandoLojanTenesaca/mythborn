package com.lautish.luckymobs.init;

import com.google.common.collect.Lists;
import com.jahirtrap.configlib.TXFConfig;
import com.lautish.luckymobs.util.LuckyStyle;

import java.util.List;

public class ModConfig extends TXFConfig {
    @Entry(min = 0, max = 100000)
    public static int luckyPassiveChance = 300;
    @Entry(min = 0, max = 100000)
    public static int luckyHostileChance = 150;
    @Entry(min = 0, max = 100000)
    public static int alphaPassiveChance = 1500;
    @Entry(min = 0, max = 100000)
    public static int alphaHostileChance = 750;
    @Entry(min = 0, max = 100000)
    public static int titanPassiveChance = 6000;
    @Entry(min = 0, max = 100000)
    public static int titanHostileChance = 3000;
    @Entry(min = 1.0, max = 4.0)
    public static double luckySize = 1.5;
    @Entry(min = 1.0, max = 4.0)
    public static double alphaSize = 2.0;
    @Entry(min = 1.0, max = 4.0)
    public static double titanSize = 3.0;
    @Entry
    public static List<String> excludedMobs = Lists.newArrayList("minecraft:ender_dragon", "minecraft:wither", "minecraft:elder_guardian", "minecraft:warden", "minecraft:villager", "minecraft:wandering_trader");
    @Entry
    public static boolean fromSpawners = false;
    @Entry
    public static boolean fromSpawnEggs = false;
    @Entry
    public static boolean fromCommands = false;
    @Entry(syncServer = true)
    public static LuckyStyle luckyStyle = LuckyStyle.GOLDEN;
    @Entry(syncServer = true)
    public static LuckyStyle alphaStyle = LuckyStyle.SOUL;
    @Entry(syncServer = true)
    public static LuckyStyle titanStyle = LuckyStyle.EMBERS;
    @Entry(min = 1.0, max = 10.0)
    public static double luckyHostileHealth = 2.0;
    @Entry(min = 1.0, max = 10.0)
    public static double luckyHostileDamage = 2.0;
    @Entry(min = 1.0, max = 10.0)
    public static double luckyHostileSpeed = 1.3;
    @Entry(min = 1.0, max = 10.0)
    public static double alphaHostileHealth = 3.0;
    @Entry(min = 1.0, max = 10.0)
    public static double alphaHostileDamage = 2.5;
    @Entry(min = 1.0, max = 10.0)
    public static double alphaHostileSpeed = 1.2;
    @Entry(min = 1.0, max = 10.0)
    public static double titanHostileHealth = 4.0;
    @Entry(min = 1.0, max = 10.0)
    public static double titanHostileDamage = 3.0;
    @Entry(min = 1.0, max = 10.0)
    public static double titanHostileSpeed = 1.0;
    @Entry(min = 1.0, max = 2.0)
    public static double luckyMountSpeed = 1.3;
    @Entry(min = 1.0, max = 2.0)
    public static double luckyMountJump = 1.2;
    @Entry(min = 1.0, max = 2.0)
    public static double alphaMountSpeed = 1.4;
    @Entry(min = 1.0, max = 2.0)
    public static double alphaMountJump = 1.3;
    @Entry(min = 1.0, max = 2.0)
    public static double titanMountSpeed = 1.5;
    @Entry(min = 1.0, max = 2.0)
    public static double titanMountJump = 1.4;
    @Entry(min = 1, max = 8)
    public static int luckyLootRolls = 2;
    @Entry(min = 1, max = 10)
    public static int luckyXpMultiplier = 2;
    @Entry(min = 1, max = 8)
    public static int alphaLootRolls = 3;
    @Entry(min = 1, max = 10)
    public static int alphaXpMultiplier = 3;
    @Entry(min = 1, max = 8)
    public static int titanLootRolls = 4;
    @Entry(min = 1, max = 10)
    public static int titanXpMultiplier = 5;
    @Entry
    public static boolean bonusLoot = true;
    @Entry
    public static boolean aggressiveAnimals = true;
    @Entry(min = 0.0, max = 40.0)
    public static double animalAttackDamage = 4.0;
    @Entry(min = 0, max = 100)
    public static int armorChanceAlpha = 25;
    @Entry(min = 0, max = 100)
    public static int armorChanceTitan = 50;
    @Entry(min = 0, max = 100)
    public static int armorDropChance = 10;
    @Entry(min = 0, max = 100)
    public static int inheritOneParent = 10;
    @Entry(min = 0, max = 100)
    public static int inheritBothParents = 50;
    @Entry
    public static boolean sparkles = true;
    @Entry
    public static boolean chime = true;
    @Entry
    public static boolean bossBar = true;
    @Entry
    public static boolean glowingOutline = false;
    @Entry
    public static boolean veil = true;
    @Entry(min = 0.0, max = 2.0)
    public static double veilStrength = 1.0;
    @Entry(min = 0, max = 15)
    public static int glowLight = 9;
    @Entry
    public static boolean showName = true;
}
