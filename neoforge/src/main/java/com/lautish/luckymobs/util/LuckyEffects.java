package com.lautish.luckymobs.util;

import com.lautish.luckymobs.init.ModConfig;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LuckyEffects {
    private static final Map<UUID, Long> CHIMED = new HashMap<>();
    private static final long CHIME_GAP = 600L;

    private record Body(ParticleOptions type, double y, double spread, double speed, int count) {
    }

    public static void tick(ServerLevel level, LivingEntity e) {
        if (!(e instanceof Mob mob) || !mob.isAlive()) return;
        LuckyTier tier = LuckyTiers.tierOf(mob);
        if (tier == null) return;
        if (ModConfig.glowingOutline) {
            MobEffectInstance glow = mob.getEffect(MobEffects.GLOWING);
            if (glow == null || glow.getDuration() < 20) mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
        }
        if (ModConfig.sparkles && level.hasNearbyAlivePlayer(mob.getX(), mob.getY(), mob.getZ(), 48.0)) sparkles(level, mob, tier);
        if (ModConfig.chime) chime(level, mob);
    }

    private static boolean named(LivingEntity e, String name) {
        return e.hasCustomName() && name.equals(e.getCustomName().getString());
    }

    private static Body[] body(LuckyStyle style, double size, double h, boolean hearts) {
        int n = (int) Math.round(size) * 2;
        double s = 0.35 * size;
        Body[] body = switch (style) {
            case GOLDEN -> new Body[]{new Body(ParticleTypes.WAX_ON, 0.5 * h, s, 0.0, n)};
            case STARRY -> new Body[]{new Body(ParticleTypes.END_ROD, 0.3 * h, s, 0.01, 2), new Body(ParticleTypes.WAX_OFF, 0.5 * h, s, 0.0, n)};
            case SOUL -> new Body[]{new Body(ParticleTypes.SOUL, 0.5 * h, s, 0.01, 2)};
            case EMBERS -> new Body[]{new Body(new DustParticleOptions(0xFF1F0D, (float) (1.0 + 0.4 * size)), 0.5 * h, s, 0.0, n), new Body(ParticleTypes.SMALL_FLAME, 0.5 * h, s, 0.01, n)};
            case TOTEM -> new Body[]{new Body(ParticleTypes.TOTEM_OF_UNDYING, 0.5 * h, s, 0.05, n)};
        };
        if (hearts) {
            for (int i = 0; i < body.length; i++) body[i] = new Body(ParticleTypes.HEART, body[i].y, body[i].spread, 0.0, Math.max(1, body[i].count / 2));
        }
        return body;
    }

    private static ParticleOptions top(LuckyStyle style) {
        return switch (style) {
            case GOLDEN -> ParticleTypes.WAX_ON;
            case STARRY -> ParticleTypes.END_ROD;
            case SOUL -> ParticleTypes.SOUL_FIRE_FLAME;
            case EMBERS -> ParticleTypes.SMALL_FLAME;
            case TOTEM -> ParticleTypes.ELECTRIC_SPARK;
        };
    }

    private static ParticleOptions feet(LuckyStyle style) {
        return switch (style) {
            case SOUL -> ParticleTypes.SOUL_FIRE_FLAME;
            case EMBERS -> ParticleTypes.FLAME;
            default -> null;
        };
    }

    private static void sparkles(ServerLevel level, Mob mob, LuckyTier tier) {
        LuckyStyle style = tier.style();
        boolean hearts = named(mob, "Jahir");
        double size = tier.size();
        double h = mob.getBbHeight();
        for (Body b : body(style, size, h, hearts)) {
            level.sendParticles(b.type, mob.getX(), mob.getY() + b.y, mob.getZ(), b.count, b.spread, b.y * 0.8, b.spread, b.speed);
        }
        ParticleOptions top = hearts ? ParticleTypes.HEART : top(style);
        ParticleOptions feet = hearts ? null : feet(style);
        for (int k = 0; k < 2; k++) {
            double a = Math.toRadians((mob.tickCount + k) * 18.0);
            ring(level, mob, top, 0.5 * size, h + 0.25, a);
            if (feet != null) ring(level, mob, feet, 0.65 * size, 0.10, a);
        }
    }

    private static void ring(ServerLevel level, Mob mob, ParticleOptions type, double r, double y, double a) {
        double dx = Math.cos(a) * r;
        double dz = Math.sin(a) * r;
        level.sendParticles(type, mob.getX() + dx, mob.getY() + y, mob.getZ() + dz, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(type, mob.getX() - dx, mob.getY() + y, mob.getZ() - dz, 1, 0.0, 0.0, 0.0, 0.0);
    }

    public static void burst(ServerLevel level, LivingEntity e, LuckyTier tier) {
        double w = e.getBbWidth();
        double h = e.getBbHeight();
        for (Body b : body(tier.style(), tier.size(), h, named(e, "Jahir"))) {
            level.sendParticles(b.type, e.getX(), e.getY() + h / 2, e.getZ(), 20, w / 2, h / 3, w / 2, Math.max(b.speed, 0.1));
        }
    }

    public static void footstep(ServerLevel level, LivingEntity e, BlockState state) {
        if (state.isAir()) return;
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), e.getX(), e.getY() + 0.1, e.getZ(), 8, e.getBbWidth() / 3, 0.05, e.getBbWidth() / 3, 0.1);
    }

    private static void chime(ServerLevel level, Mob mob) {
        if (mob.hasCustomName() || mob.isVehicle() || mob.isPassenger()) return;
        if (mob instanceof OwnableEntity ownable && ownable.getOwnerReference() != null) return;
        if (level.getNearestPlayer(mob, 16.0) == null) return;
        long now = level.getGameTime();
        Long last = CHIMED.get(mob.getUUID());
        if (last != null && now - last < CHIME_GAP) return;
        if (CHIMED.size() > 256) CHIMED.values().removeIf(t -> now - t >= CHIME_GAP);
        CHIMED.put(mob.getUUID(), now);
        level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 0.5F, 1.0F);
    }
}
