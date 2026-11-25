package com.lautish.alphamobs.init.mixin;

import com.lautish.alphamobs.display.LuckyLayer;
import com.lautish.alphamobs.display.LuckyRenderState;
import com.lautish.alphamobs.init.ModConfig;
import com.lautish.alphamobs.util.LuckyTier;
import com.lautish.alphamobs.util.LuckyTiers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Shadow
    protected abstract boolean addLayer(RenderLayer<S, M> layer);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addLuckyLayer(EntityRendererProvider.Context context, M model, float shadow, CallbackInfo ci) {
        addLayer(new LuckyLayer<>((LivingEntityRenderer<?, S, M>) (Object) this));
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void extractLucky(T entity, S state, float partialTicks, CallbackInfo ci) {
        LuckyTier tier = LuckyTiers.tierOf(entity);
        int veil = -1;
        if (tier != null && ModConfig.veil) {
            boolean rainbow = entity.hasCustomName() && "Lautish".equals(entity.getCustomName().getString());
            veil = rainbow ? LuckyLayer.RAINBOW : tier.style().color();
        }
        ((LuckyRenderState) state).setVeil(veil);
    }
}
