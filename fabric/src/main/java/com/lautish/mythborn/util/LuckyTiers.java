package com.lautish.mythborn.util;

import com.lautish.mythborn.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
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

    private static final double HOSTILE_SPEED_CAP = 0.3;

    private static final String[] EFFECTS = {"health", "speed", "knockback", "damage", "attack_knockback", "fall", "mount_speed", "mount_jump", "mount_fall"};
    private static final List<Holder<Attribute>> ATTRIBUTES = List.of(Attributes.MAX_HEALTH, Attributes.MOVEMENT_SPEED, Attributes.KNOCKBACK_RESISTANCE, Attributes.ATTACK_DAMAGE, Attributes.ATTACK_KNOCKBACK, Attributes.FALL_DAMAGE_MULTIPLIER, Attributes.JUMP_STRENGTH, Attributes.SAFE_FALL_DISTANCE);

    public static @Nullable LuckyTier tierOf(Entity entity) {
        if (!(entity instanceof LivingEntity e)) return null;
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
        health(mob, tier);
        if (tier == LuckyTier.TITAN) multiply(mob, Attributes.FALL_DAMAGE_MULTIPLIER, tier, "fall", 0.0);
        if (mob.getType().getCategory() == MobCategory.MONSTER) {
            speed(mob, tier);
            multiply(mob, Attributes.ATTACK_DAMAGE, tier, "damage", tier.hostileDamage());
            add(mob, Attributes.KNOCKBACK_RESISTANCE, tier, "knockback", tier.knockbackResistance(), AttributeModifier.Operation.ADD_VALUE);
        }
        if (LuckyCombat.canFight(mob, tier)) {
            add(mob, Attributes.ATTACK_DAMAGE, tier, "damage", LuckyCombat.ANIMAL_DAMAGE * tier.hostileDamage(), AttributeModifier.Operation.ADD_VALUE);
            add(mob, Attributes.ATTACK_KNOCKBACK, tier, "attack_knockback", LuckyCombat.ANIMAL_KNOCKBACK, AttributeModifier.Operation.ADD_VALUE);
        }
        if (mob.is(ModTags.EntityTypes.LUCKY_MOUNTS)) {
            multiply(mob, Attributes.MOVEMENT_SPEED, tier, "mount_speed", tier.mountSpeed());
            multiply(mob, Attributes.JUMP_STRENGTH, tier, "mount_jump", tier.mountJump());
            multiply(mob, Attributes.SAFE_FALL_DISTANCE, tier, "mount_fall", tier.mountJump() * tier.mountJump());
        }
        mob.setHealth(mob.getMaxHealth());
        LuckyCombat.addGoals(mob, tier);
        LuckyCombat.armor(mob, tier);
    }

    private static void speed(Mob mob, LuckyTier tier) {
        double base = mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
        double target = Math.max(base, Math.min(base * tier.hostileSpeed(), HOSTILE_SPEED_CAP));
        add(mob, Attributes.MOVEMENT_SPEED, tier, "speed", target - base, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void health(Mob mob, LuckyTier tier) {
        double base = mob.getAttributeBaseValue(Attributes.MAX_HEALTH);
        double scaled = mob.getType().getCategory() == MobCategory.MONSTER ? base * tier.hostileHealth() : base;
        add(mob, Attributes.MAX_HEALTH, tier, "health", Math.max(scaled, tier.minHealth()) - base, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void multiply(Mob mob, Holder<Attribute> attribute, LuckyTier tier, String effect, double factor) {
        add(mob, attribute, tier, effect, factor - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static void add(Mob mob, Holder<Attribute> attribute, LuckyTier tier, String effect, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null && amount != 0.0) instance.addPermanentModifier(new AttributeModifier(tier.modifierId(effect), amount, operation));
    }

    public static double indirectDamageMultiplier(DamageSource source) {
        if (source.getDirectEntity() == source.getEntity()) return 1.0;
        if (!(source.getEntity() instanceof LivingEntity living) || living.getType().getCategory() != MobCategory.MONSTER) return 1.0;
        LuckyTier tier = tierOf(living);
        return tier == null ? 1.0 : tier.hostileDamage();
    }

    public static void rewardBurst(ServerLevel level, LivingEntity e) {
        LuckyTier tier = tierOf(e);
        if (tier == null) return;
        LuckyEffects.burst(level, e, tier);
        level.playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.PLAYER_LEVELUP, e.getSoundSource(), 1.0F, 1.0F);
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
        return !mob.is(ModTags.EntityTypes.CANNOT_BE_LUCKY);
    }

    public static @Nullable LuckyTier roll(Mob mob, EntitySpawnReason reason, RandomSource random) {
        if (!allowed(reason)) return null;
        for (LuckyTier tier : ROLL_ORDER) {
            if (tier.chance() > 0 && random.nextInt(tier.chance()) == 0) return tier;
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
            case BREEDING, CONVERSION, LOAD, DIMENSION_TRAVEL -> false;
            default -> true;
        };
    }
}
