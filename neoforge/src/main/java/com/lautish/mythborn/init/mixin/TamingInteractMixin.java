package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.parrot.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({Parrot.class, Ocelot.class})
public abstract class TamingInteractMixin {
    @ModifyArg(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"), index = 0)
    private int harderToTame(int bound) {
        LuckyTier tier = LuckyTiers.tierOf((Mob) (Object) this);
        return tier == null ? bound : bound * tier.tameDifficulty();
    }
}
