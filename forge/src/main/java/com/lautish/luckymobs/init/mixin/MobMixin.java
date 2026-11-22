package com.lautish.luckymobs.init.mixin;

import com.lautish.luckymobs.util.LuckyTier;
import com.lautish.luckymobs.util.LuckyTiers;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void rollTier(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob mob = (Mob) (Object) this;
        if (!LuckyTiers.eligible(mob)) return;
        LuckyTier tier = LuckyTiers.roll(mob, reason, level.getRandom());
        while (tier != null && !LuckyTiers.fits(mob, tier.size(), level)) tier = tier.lower();
        if (tier != null) LuckyTiers.apply(mob, tier);
    }
}
