package com.lautish.luckymobs.util;

import com.lautish.luckymobs.init.ModConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

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

    public double hostileHealth() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyHostileHealth;
            case ALPHA -> ModConfig.alphaHostileHealth;
            case TITAN -> ModConfig.titanHostileHealth;
        };
    }

    public double hostileDamage() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyHostileDamage;
            case ALPHA -> ModConfig.alphaHostileDamage;
            case TITAN -> ModConfig.titanHostileDamage;
        };
    }

    public double hostileSpeed() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyHostileSpeed;
            case ALPHA -> ModConfig.alphaHostileSpeed;
            case TITAN -> ModConfig.titanHostileSpeed;
        };
    }

    public double mountSpeed() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyMountSpeed;
            case ALPHA -> ModConfig.alphaMountSpeed;
            case TITAN -> ModConfig.titanMountSpeed;
        };
    }

    public double mountJump() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyMountJump;
            case ALPHA -> ModConfig.alphaMountJump;
            case TITAN -> ModConfig.titanMountJump;
        };
    }

    public double knockbackResistance() {
        return switch (this) {
            case LUCKY -> 0.0;
            case ALPHA -> 0.5;
            case TITAN -> 1.0;
        };
    }

    public ResourceKey<LootTable> bonusTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(MODID, "entities/" + name().toLowerCase(Locale.ROOT) + "_bonus"));
    }

    public Identifier modifierId(String effect) {
        return Identifier.fromNamespaceAndPath(MODID, name().toLowerCase(Locale.ROOT) + "_" + effect);
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
