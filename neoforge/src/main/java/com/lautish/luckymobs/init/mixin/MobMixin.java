package com.lautish.luckymobs.init.mixin;

import com.lautish.luckymobs.util.LuckyTier;
import com.lautish.luckymobs.util.LuckyTiers;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.ConversionType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void rollTier(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob mob = (Mob) (Object) this;
        if (!(mob instanceof AbstractCubeMob)) LuckyTiers.rollAndApply(mob, reason, level);
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void noSunBurn(CallbackInfoReturnable<Boolean> cir) {
        LuckyTier tier = LuckyTiers.tierOf((Mob) (Object) this);
        if (tier == LuckyTier.ALPHA || tier == LuckyTier.TITAN) cir.setReturnValue(false);
    }

    @Inject(method = "requiresCustomPersistence", at = @At("HEAD"), cancellable = true)
    private void keepLoaded(CallbackInfoReturnable<Boolean> cir) {
        LuckyTier tier = LuckyTiers.tierOf((Mob) (Object) this);
        if (tier == LuckyTier.ALPHA || tier == LuckyTier.TITAN) cir.setReturnValue(true);
    }

    @Inject(method = "convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;", at = @At("RETURN"))
    private void keepTier(EntityType<?> type, ConversionParams params, EntitySpawnReason reason, ConversionParams.AfterConversion<?> after, CallbackInfoReturnable<Mob> cir) {
        Mob result = cir.getReturnValue();
        LuckyTier tier = LuckyTiers.tierOf((Mob) (Object) this);
        if (result == null || tier == null || params.type() == ConversionType.SPLIT_ON_DEATH || !LuckyTiers.eligible(result)) return;
        while (tier != null && !LuckyTiers.fits(result, tier.size())) tier = tier.lower();
        if (tier != null) LuckyTiers.apply(result, tier);
    }
}
