package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyEffects;
import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void tierName(CallbackInfoReturnable<Component> cir) {
        Entity self = (Entity) (Object) this;
        if (self.hasCustomName()) return;
        LuckyTier tier = LuckyTiers.tierOf(self);
        if (tier != null) cir.setReturnValue(tier.displayName(cir.getReturnValue()));
    }

    @Inject(method = "walkingStepSound", at = @At("HEAD"))
    private void titanDust(BlockPos pos, BlockState state, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.level() instanceof ServerLevel level && self instanceof LivingEntity living && LuckyTiers.tierOf(living) == LuckyTier.TITAN) LuckyEffects.footstep(level, living, state);
    }
}
