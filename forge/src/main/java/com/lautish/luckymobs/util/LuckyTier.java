package com.lautish.luckymobs.util;

import com.lautish.luckymobs.init.ModConfig;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import static com.lautish.luckymobs.LuckyMobsMod.MODID;

public enum LuckyTier {
    LUCKY("lucky"),
    ALPHA("alpha"),
    TITAN("titan");

    private final Identifier id;

    LuckyTier(String name) {
        this.id = Identifier.fromNamespaceAndPath(MODID, name);
    }

    public Identifier id() {
        return id;
    }

    public double size() {
        return switch (this) {
            case LUCKY -> ModConfig.luckySize;
            case ALPHA -> ModConfig.alphaSize;
            case TITAN -> ModConfig.titanSize;
        };
    }

    public int passiveChance() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyPassiveChance;
            case ALPHA -> ModConfig.alphaPassiveChance;
            case TITAN -> ModConfig.titanPassiveChance;
        };
    }

    public int hostileChance() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyHostileChance;
            case ALPHA -> ModConfig.alphaHostileChance;
            case TITAN -> ModConfig.titanHostileChance;
        };
    }

    public int lootRolls() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyLootRolls;
            case ALPHA -> ModConfig.alphaLootRolls;
            case TITAN -> ModConfig.titanLootRolls;
        };
    }

    public double xpMultiplier() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyXpMultiplier;
            case ALPHA -> ModConfig.alphaXpMultiplier;
            case TITAN -> ModConfig.titanXpMultiplier;
        };
    }

    public LuckyStyle style() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyStyle;
            case ALPHA -> ModConfig.alphaStyle;
            case TITAN -> ModConfig.titanStyle;
        };
    }

    public @Nullable LuckyTier lower() {
        return switch (this) {
            case TITAN -> ALPHA;
            case ALPHA -> LUCKY;
            case LUCKY -> null;
        };
    }
}
