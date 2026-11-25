package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({Wolf.class, Cat.class, AbstractNautilus.class})
public abstract class TamingMixin {
    @ModifyArg(method = "tryToTame", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"), index = 0)
    private int harderToTame(int bound) {
        LuckyTier tier = LuckyTiers.tierOf((Mob) (Object) this);
        return tier == null ? bound : bound * tier.tameDifficulty();
    }
}
