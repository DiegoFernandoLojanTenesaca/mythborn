package com.lautish.alphamobs.init.mixin;

import com.lautish.alphamobs.util.LuckyEffects;
import com.lautish.alphamobs.util.LuckyTier;
import com.lautish.alphamobs.util.LuckyTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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

    @ModifyVariable(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float titanVolume(float volume) {
        return LuckyTiers.tierOf((Entity) (Object) this) == LuckyTier.TITAN ? volume * 1.25F : volume;
    }

    @ModifyVariable(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private float deepVoice(float pitch) {
        LuckyTier tier = LuckyTiers.tierOf((Entity) (Object) this);
        return tier == null ? pitch : pitch / (float) Math.sqrt(tier.size());
    }

    @Inject(method = "walkingStepSound", at = @At("HEAD"))
    private void titanDust(BlockPos pos, BlockState state, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.level() instanceof ServerLevel level && self instanceof LivingEntity living && LuckyTiers.tierOf(living) == LuckyTier.TITAN) LuckyEffects.footstep(level, living, state);
    }
}
