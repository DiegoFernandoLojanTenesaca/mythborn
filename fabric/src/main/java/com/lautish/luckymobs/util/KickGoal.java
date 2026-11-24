package com.lautish.luckymobs.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class KickGoal extends Goal {
    private final Mob mob;
    private int cooldown;

    public KickGoal(Mob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) cooldown--;
        LivingEntity target = mob.getTarget();
        return cooldown <= 0 && LuckyCombat.active(mob) && LuckyCombat.canTarget(mob, target) && mob.isWithinMeleeAttackRange(target) && mob.getSensing().hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        LuckyCombat.hit(mob, target, 0.5F, 0.4F);
        cooldown = 30;
    }
}
