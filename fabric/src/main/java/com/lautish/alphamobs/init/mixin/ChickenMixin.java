package com.lautish.alphamobs.init.mixin;

import com.lautish.alphamobs.util.LuckyTiers;
import net.minecraft.world.entity.animal.chicken.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public abstract class ChickenMixin {
    @Shadow
    public int eggTime;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void layFaster(CallbackInfo ci) {
        if (eggTime > 1 && LuckyTiers.tierOf((Chicken) (Object) this) != null) eggTime--;
    }
}
