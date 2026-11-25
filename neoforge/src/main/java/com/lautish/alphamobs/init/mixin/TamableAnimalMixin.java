package com.lautish.alphamobs.init.mixin;

import com.lautish.alphamobs.util.LuckyTiers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin {
    @Inject(method = "tame", at = @At("RETURN"))
    private void reward(Player player, CallbackInfo ci) {
        TamableAnimal self = (TamableAnimal) (Object) this;
        if (self.level() instanceof ServerLevel level) LuckyTiers.rewardBurst(level, self);
    }
}
