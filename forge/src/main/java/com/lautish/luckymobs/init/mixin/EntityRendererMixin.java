package com.lautish.luckymobs.init.mixin;

import com.lautish.luckymobs.init.ModConfig;
import com.lautish.luckymobs.util.LuckyTiers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Shadow
    @Final
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    protected abstract Component getNameTag(T entity);

    @Inject(method = "getBlockLightLevel", at = @At("RETURN"), cancellable = true)
    private void glow(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.glowLight <= 0 || LuckyTiers.tierOf(entity) == null) return;
        int pulse = 9 + (int) (6 * (0.5 + 0.5 * Math.sin(entity.tickCount * 0.12)));
        cir.setReturnValue(Math.max(cir.getReturnValue(), Math.max(ModConfig.glowLight, pulse)));
    }

    @Inject(method = "extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FDD)V", at = @At("TAIL"))
    private void showTierName(T entity, S state, float partialTicks, double nameTagDistance, double belowNameDistance, CallbackInfo ci) {
        if (!ModConfig.showName || state.nameTag != null || entity != entityRenderDispatcher.crosshairPickEntity) return;
        if (LuckyTiers.tierOf(entity) == null || state.distanceToCameraSq >= nameTagDistance * nameTagDistance) return;
        state.nameTag = getNameTag(entity);
        state.nameTagAttachment = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTicks));
    }
}
