package com.lautish.luckymobs.util;

import com.lautish.luckymobs.init.ModConfig;
import com.lautish.luckymobs.init.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.CollisionGetter;
import org.jspecify.annotations.Nullable;

public class LuckyTiers {
    private static final LuckyTier[] ROLL_ORDER = {LuckyTier.TITAN, LuckyTier.ALPHA, LuckyTier.LUCKY};

    public static @Nullable LuckyTier tierOf(LivingEntity e) {
        AttributeInstance scale = e.getAttribute(Attributes.SCALE);
        if (scale == null) return null;
        for (LuckyTier tier : ROLL_ORDER) {
            if (scale.hasModifier(tier.id())) return tier;
        }
        return null;
    }

    public static void apply(Mob mob, LuckyTier tier) {
        clear(mob);
        AttributeInstance scale = mob.getAttribute(Attributes.SCALE);
        if (scale == null) return;
        scale.addPermanentModifier(new AttributeModifier(tier.id(), tier.size() - 1.0, AttributeModifier.Operation.ADD_VALUE));
        mob.setHealth(mob.getMaxHealth());
    }

    public static void clear(LivingEntity e) {
        AttributeInstance scale = e.getAttribute(Attributes.SCALE);
        if (scale == null) return;
        for (LuckyTier tier : ROLL_ORDER) {
            scale.removeModifier(tier.id());
        }
    }

    public static boolean eligible(Mob mob) {
        EntityType<?> type = mob.getType();
        if (type.getCategory() == MobCategory.MISC && !mob.is(ModTags.EntityTypes.CAN_BE_LUCKY)) return false;
        if (mob.is(ModTags.EntityTypes.CANNOT_BE_LUCKY)) return false;
        return !ModConfig.excludedMobs.contains(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }

    public static @Nullable LuckyTier roll(Mob mob, EntitySpawnReason reason, RandomSource random) {
        if (!allowed(reason)) return null;
        boolean hostile = mob.getType().getCategory() == MobCategory.MONSTER;
        for (LuckyTier tier : ROLL_ORDER) {
            int chance = hostile ? tier.hostileChance() : tier.passiveChance();
            if (chance > 0 && random.nextInt(chance) == 0) return tier;
        }
        return null;
    }

    public static boolean fits(Mob mob, double size) {
        return fits(mob, size, mob.level());
    }

    public static boolean fits(Mob mob, double size, CollisionGetter level) {
        return level.noBlockCollision(mob, mob.getDimensions(mob.getPose()).scale((float) size).makeBoundingBox(mob.position()));
    }

    private static boolean allowed(EntitySpawnReason reason) {
        return switch (reason) {
            case NATURAL, CHUNK_GENERATION, STRUCTURE, PATROL -> true;
            case SPAWNER, TRIAL_SPAWNER -> ModConfig.fromSpawners;
            case SPAWN_ITEM_USE, DISPENSER -> ModConfig.fromSpawnEggs;
            case COMMAND -> ModConfig.fromCommands;
            default -> false;
        };
    }
}
