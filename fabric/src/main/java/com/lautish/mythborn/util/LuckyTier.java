package com.lautish.mythborn.util;

import com.lautish.mythborn.init.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

import static com.lautish.mythborn.MythbornMod.MODID;

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
            case LUCKY -> 1.5;
            case ALPHA -> 2.0;
            case TITAN -> 3.0;
        };
    }

    public int chance() {
        return switch (this) {
            case LUCKY -> ModConfig.luckyChance;
            case ALPHA -> ModConfig.alphaChance;
            case TITAN -> ModConfig.titanChance;
        };
    }

    public int lootRolls() {
        return switch (this) {
            case LUCKY -> 2;
            case ALPHA -> 3;
            case TITAN -> 4;
        };
    }

    public double xpMultiplier() {
        return switch (this) {
            case LUCKY -> 2.0;
            case ALPHA -> 3.0;
            case TITAN -> 5.0;
        };
    }

    public double hostileHealth() {
        return switch (this) {
            case LUCKY -> 2.0;
            case ALPHA -> 3.0;
            case TITAN -> 4.0;
        };
    }

    public double hostileDamage() {
        return switch (this) {
            case LUCKY -> 2.0;
            case ALPHA -> 2.5;
            case TITAN -> 3.0;
        };
    }

    public double hostileSpeed() {
        return switch (this) {
            case LUCKY -> 1.3;
            case ALPHA -> 1.2;
            case TITAN -> 1.0;
        };
    }

    public double mountSpeed() {
        return switch (this) {
            case LUCKY -> 1.3;
            case ALPHA -> 1.4;
            case TITAN -> 1.5;
        };
    }

    public double mountJump() {
        return switch (this) {
            case LUCKY -> 1.2;
            case ALPHA -> 1.3;
            case TITAN -> 1.4;
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

    public ChatFormatting formatting() {
        return switch (this) {
            case LUCKY -> ChatFormatting.GOLD;
            case ALPHA -> ChatFormatting.AQUA;
            case TITAN -> ChatFormatting.RED;
        };
    }

    public Component displayName(Component typeName) {
        String word = switch (this) {
            case LUCKY -> "Lucky";
            case ALPHA -> "Alpha";
            case TITAN -> "Titan";
        };
        return Component.translatableWithFallback("mythborn.name." + id.getPath(), word + " %s", typeName).withStyle(formatting());
    }

    public @Nullable LuckyTier higher() {
        return switch (this) {
            case LUCKY -> ALPHA;
            case ALPHA -> TITAN;
            case TITAN -> null;
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
