package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.init.ModConfig;
import com.lautish.mythborn.util.LuckyCombat;
import com.lautish.mythborn.util.LuckyEffects;
import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected int lastHurtByPlayerMemoryTime;

    @Unique
    private long nextRetaliation;

    @Shadow
    protected abstract void dropFromLootTable(ServerLevel level, DamageSource source, boolean playerKilled);

    @Inject(method = "tick", at = @At("TAIL"))
    private void effects(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level() instanceof ServerLevel level && self.tickCount % 2 == 0) LuckyEffects.tick(level, self);
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void burst(ServerLevel level, DamageSource source, CallbackInfo ci) {
        LuckyTiers.rewardBurst(level, (LivingEntity) (Object) this);
    }

    @Inject(method = "dropAllDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V", shift = At.Shift.AFTER))
    private void extraLoot(ServerLevel level, DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        LuckyTier tier = LuckyTiers.tierOf(self);
        if (tier == null) return;
        boolean playerKilled = lastHurtByPlayerMemoryTime > 0;
        for (int i = 1; i < tier.lootRolls(); i++) {
            dropFromLootTable(level, source, playerKilled);
        }
        if (ModConfig.bonusLoot) self.dropFromLootTable(level, source, playerKilled, tier.bonusTable());
    }

    @Inject(method = "getExperienceReward", at = @At("RETURN"), cancellable = true)
    private void multiplyExperience(ServerLevel level, @Nullable Entity killer, CallbackInfoReturnable<Integer> cir) {
        LuckyTier tier = LuckyTiers.tierOf((LivingEntity) (Object) this);
        if (tier != null) cir.setReturnValue((int) Math.round(cir.getReturnValueI() * tier.xpMultiplier()));
    }

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float multiplyDamage(float dmg, ServerLevel level, DamageSource source) {
        return (float) (dmg * LuckyTiers.damageMultiplier(source.getEntity()));
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void titanFalls(ServerLevel level, DamageSource source, float dmg, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypeTags.IS_FALL) && LuckyTiers.tierOf((LivingEntity) (Object) this) == LuckyTier.TITAN) cir.setReturnValue(false);
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void temper(ServerLevel level, DamageSource source, float dmg, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && (Object) this instanceof Mob mob && level.getGameTime() >= nextRetaliation && LuckyCombat.retaliate(mob, source, level)) {
            nextRetaliation = level.getGameTime() + LuckyCombat.RETALIATE_COOLDOWN;
        }
    }
}
