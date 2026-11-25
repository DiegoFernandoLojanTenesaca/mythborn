package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Llama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractHorse.class, Llama.class})
public abstract class MaxTemperMixin {
    @Inject(method = "getMaxTemper", at = @At("RETURN"), cancellable = true)
    private void harderToTame(CallbackInfoReturnable<Integer> cir) {
        LuckyTier tier = LuckyTiers.tierOf((Mob) (Object) this);
        if (tier != null) cir.setReturnValue(cir.getReturnValueI() * tier.tameDifficulty());
    }
}
