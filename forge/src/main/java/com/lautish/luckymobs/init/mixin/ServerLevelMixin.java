package com.lautish.luckymobs.init.mixin;

import com.lautish.luckymobs.util.LuckyBossBars;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void bossBars(BooleanSupplier haveTime, CallbackInfo ci) {
        LuckyBossBars.tick((ServerLevel) (Object) this);
    }
}
