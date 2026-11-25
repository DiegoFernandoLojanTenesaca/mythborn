package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyCombat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RunAroundLikeCrazyGoal.class)
public abstract class RunAroundLikeCrazyGoalMixin {
    @Shadow
    @Final
    private AbstractHorse horse;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;ejectPassengers()V"))
    private void buckWarning(CallbackInfo ci) {
        if (horse.getFirstPassenger() instanceof LivingEntity rider) LuckyCombat.warn(horse, rider);
    }
}
