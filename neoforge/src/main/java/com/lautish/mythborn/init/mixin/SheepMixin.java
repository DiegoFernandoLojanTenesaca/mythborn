package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyTier;
import com.lautish.mythborn.util.LuckyTiers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sheep.class)
public abstract class SheepMixin extends Animal {
    protected SheepMixin(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "shear", at = @At("HEAD"))
    private void moreWool(ServerLevel level, SoundSource source, ItemStack tool, CallbackInfo ci) {
        LuckyTier tier = LuckyTiers.tierOf(this);
        if (tier == null) return;
        for (int i = 1; i < tier.lootRolls(); i++) {
            dropFromShearingLootTable(level, BuiltInLootTables.SHEAR_SHEEP, tool, (l, drop) -> {
                for (int n = 0; n < drop.getCount(); n++) spawnAtLocation(l, drop.copyWithCount(1), 1.0F);
            });
        }
    }
}
