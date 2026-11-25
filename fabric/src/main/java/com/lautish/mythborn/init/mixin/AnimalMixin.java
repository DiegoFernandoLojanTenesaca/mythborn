package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.init.ModConfig;
import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {
    @Inject(method = "finalizeSpawnChildFromBreeding", at = @At("RETURN"))
    private void inheritLuck(ServerLevel level, Animal partner, @Nullable AgeableMob offspring, CallbackInfo ci) {
        if (!(offspring instanceof Mob baby) || !LuckyTiers.eligible(baby)) return;
        boolean mine = LuckyTiers.tierOf((Animal) (Object) this) != null;
        boolean theirs = LuckyTiers.tierOf(partner) != null;
        if (!mine && !theirs) return;
        int chance = mine && theirs ? ModConfig.inheritBothParents : ModConfig.inheritOneParent;
        if (level.getRandom().nextInt(100) < chance) LuckyTiers.apply(baby, LuckyTier.LUCKY);
    }
}
