package com.lautish.luckymobs.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class StompGoal extends Goal {
    private static final double RADIUS = 4.0;
    private final Mob mob;
    private int cooldown;
    private int ticks;
    private boolean landed;

    public StompGoal(Mob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) cooldown--;
        LivingEntity target = mob.getTarget();
        if (cooldown > 0 || LuckyTiers.tierOf(mob) != LuckyTier.TITAN || !mob.onGround() || !LuckyCombat.active(mob) || !LuckyCombat.canTarget(mob, target)) return false;
        double distance = mob.distanceToSqr(target);
        return distance > 9.0 && distance < 64.0;
    }

    @Override
    public boolean canContinueToUse() {
        return !landed && ticks < 40;
    }

    @Override
    public void start() {
        ticks = 0;
        landed = false;
        LivingEntity target = mob.getTarget();
        Vec3 dir = new Vec3(target.getX() - mob.getX(), 0.0, target.getZ() - mob.getZ()).normalize().scale(0.5);
        mob.getNavigation().stop();
        mob.setDeltaMovement(dir.x, 0.9, dir.z);
    }

    @Override
    public void stop() {
        cooldown = 160;
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks > 4 && mob.onGround() && mob.level() instanceof ServerLevel level) {
            landed = true;
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(RADIUS, 1.0, RADIUS))) {
                if (e != mob && e.distanceToSqr(mob) <= RADIUS * RADIUS && (e instanceof Player || e == mob.getTarget()) && LuckyCombat.canTarget(mob, e)) {
                    LuckyCombat.hit(mob, e, 0.5F, 1.0F);
                }
            }
            level.sendParticles(ParticleTypes.EXPLOSION, mob.getX(), mob.getY() + 0.2, mob.getZ(), 3, 1.5, 0.1, 1.5, 0.0);
            mob.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.8F);
        }
    }
}
