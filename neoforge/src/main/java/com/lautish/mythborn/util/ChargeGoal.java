package com.lautish.mythborn.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ChargeGoal extends Goal {
    private final Mob mob;
    private int cooldown;
    private int ticks;
    private boolean hit;

    public ChargeGoal(Mob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) cooldown--;
        LivingEntity target = mob.getTarget();
        return cooldown <= 0 && LuckyCombat.active(mob) && LuckyCombat.canTarget(mob, target) && mob.distanceToSqr(target) < 400.0;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return !hit && ticks < 60 && LuckyCombat.active(mob) && LuckyCombat.canTarget(mob, target) && mob.distanceToSqr(target) < 1024.0;
    }

    @Override
    public void start() {
        ticks = 0;
        hit = false;
    }

    @Override
    public void stop() {
        cooldown = 60;
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        ticks++;
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (ticks % 4 == 1) mob.getNavigation().moveTo(target, 1.6);
        double reach = mob.getBbWidth() / 2 + target.getBbWidth() / 2 + 1.0;
        if ((mob.isWithinMeleeAttackRange(target) || mob.distanceToSqr(target) < reach * reach) && mob.getSensing().hasLineOfSight(target)) {
            LuckyCombat.hit(mob, target, 0.35F, 1.2F);
            hit = true;
        }
    }
}
