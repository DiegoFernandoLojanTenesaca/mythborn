package com.lautish.alphamobs.init.mixin;

import com.lautish.alphamobs.util.LuckyTiers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin {
    @Inject(method = "tameWithName", at = @At("RETURN"))
    private void reward(Player player, CallbackInfoReturnable<Boolean> cir) {
        AbstractHorse self = (AbstractHorse) (Object) this;
        if (self.level() instanceof ServerLevel level) LuckyTiers.rewardBurst(level, self);
    }
}
