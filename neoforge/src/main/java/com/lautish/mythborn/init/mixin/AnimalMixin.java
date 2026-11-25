package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {
    @Inject(method = "finalizeSpawnChildFromBreeding", at = @At("RETURN"))
    private void inheritLuck(ServerLevel level, Animal partner, @Nullable AgeableMob offspring, CallbackInfo ci) {
        if (!(offspring instanceof Mob baby) || !LuckyTiers.eligible(baby)) return;
        LuckyTier tier = inherited(LuckyTiers.tierOf((Animal) (Object) this), LuckyTiers.tierOf(partner), level.getRandom());
        while (tier != null && !LuckyTiers.fits(baby, tier.size())) tier = tier.lower();
        if (tier != null) LuckyTiers.apply(baby, tier);
    }

    @Unique
    private static @Nullable LuckyTier inherited(@Nullable LuckyTier mine, @Nullable LuckyTier theirs, RandomSource random) {
        if (mine == null || theirs == null) return random.nextBoolean() ? (mine != null ? mine : theirs) : null;
        if (mine != theirs) return random.nextBoolean() ? mine : theirs;
        return random.nextInt(10) == 0 && mine.higher() != null ? mine.higher() : mine;
    }
}
