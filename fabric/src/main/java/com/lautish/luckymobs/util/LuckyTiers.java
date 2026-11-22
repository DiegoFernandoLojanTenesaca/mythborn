package com.lautish.luckymobs.util;

import com.lautish.luckymobs.init.ModConfig;
import com.lautish.luckymobs.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
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
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class LuckyTiers {
    private static final LuckyTier[] ROLL_ORDER = {LuckyTier.TITAN, LuckyTier.ALPHA, LuckyTier.LUCKY};

    private static final String[] EFFECTS = {"health", "speed", "knockback", "mount_speed", "mount_jump"};
    private static final List<Holder<Attribute>> ATTRIBUTES = List.of(Attributes.MAX_HEALTH, Attributes.MOVEMENT_SPEED, Attributes.KNOCKBACK_RESISTANCE, Attributes.JUMP_STRENGTH);

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
        if (mob.getType().getCategory() == MobCategory.MONSTER) {
            multiply(mob, Attributes.MAX_HEALTH, tier, "health", tier.hostileHealth());
            multiply(mob, Attributes.MOVEMENT_SPEED, tier, "speed", tier.hostileSpeed());
            add(mob, Attributes.KNOCKBACK_RESISTANCE, tier, "knockback", tier.knockbackResistance(), AttributeModifier.Operation.ADD_VALUE);
        }
        if (mob.is(ModTags.EntityTypes.LUCKY_MOUNTS)) {
            multiply(mob, Attributes.MOVEMENT_SPEED, tier, "mount_speed", tier.mountSpeed());
            multiply(mob, Attributes.JUMP_STRENGTH, tier, "mount_jump", tier.mountJump());
        }
        mob.setHealth(mob.getMaxHealth());
    }

    private static void multiply(Mob mob, Holder<Attribute> attribute, LuckyTier tier, String effect, double factor) {
        add(mob, attribute, tier, effect, factor - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static void add(Mob mob, Holder<Attribute> attribute, LuckyTier tier, String effect, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null && amount != 0.0) instance.addPermanentModifier(new AttributeModifier(tier.modifierId(effect), amount, operation));
    }

    public static double damageMultiplier(@Nullable Entity attacker) {
        if (!(attacker instanceof LivingEntity living) || living.getType().getCategory() != MobCategory.MONSTER) return 1.0;
        LuckyTier tier = tierOf(living);
        return tier == null ? 1.0 : tier.hostileDamage();
    }

    public static void rewardBurst(ServerLevel level, LivingEntity e) {
        LuckyTier tier = tierOf(e);
        if (tier == null) return;
        double w = e.getBbWidth();
        level.sendParticles(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFF000000 | tier.style().color()), e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ(), 40, w / 2, e.getBbHeight() / 3, w / 2, 0.1);
        e.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    public static void clear(LivingEntity e) {
        AttributeInstance scale = e.getAttribute(Attributes.SCALE);
        if (scale == null) return;
        for (LuckyTier tier : ROLL_ORDER) {
            scale.removeModifier(tier.id());
            for (String effect : EFFECTS) {
                for (Holder<Attribute> attribute : ATTRIBUTES) {
                    AttributeInstance instance = e.getAttribute(attribute);
                    if (instance != null) instance.removeModifier(tier.modifierId(effect));
                }
            }
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

    public static void rollAndApply(Mob mob, EntitySpawnReason reason, ServerLevelAccessor level) {
        if (!eligible(mob)) return;
        LuckyTier tier = roll(mob, reason, level.getRandom());
        while (tier != null && !fits(mob, tier.size(), level)) tier = tier.lower();
        if (tier != null) apply(mob, tier);
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
