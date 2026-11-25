package com.lautish.mythborn.init.mixin;

import com.lautish.mythborn.util.LuckyBossBars;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Unique
    private final Map<UUID, ServerBossEvent> luckyBars = new HashMap<>();

    @Inject(method = "tick", at = @At("RETURN"))
    private void bossBars(BooleanSupplier haveTime, CallbackInfo ci) {
        LuckyBossBars.tick((ServerLevel) (Object) this, luckyBars);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void clearBars(CallbackInfo ci) {
        luckyBars.values().forEach(ServerBossEvent::removeAllPlayers);
        luckyBars.clear();
    }
}
