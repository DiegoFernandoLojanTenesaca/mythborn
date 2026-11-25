package com.lautish.mythborn.display;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;

public class LuckyLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    public static final int RAINBOW = -2;

    private final LivingEntityRenderer<?, S, M> renderer;

    public LuckyLayer(LivingEntityRenderer<?, S, M> renderer) {
        super((RenderLayerParent<S, M>) renderer);
        this.renderer = renderer;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, S state, float yRot, float xRot) {
        int veil = ((LuckyRenderState) state).veil();
        if (veil == -1 || state.isInvisible) return;
        int rgb = veil == RAINBOW ? Mth.hsvToRgb((state.ageInTicks * 0.01F) % 1.0F, 0.75F, 1.0F) : veil;
        float pulse = 0.5F + 0.5F * (float) Math.sin(state.ageInTicks * 0.12F);
        int alpha = (int) (45 + 50 * pulse);
        int color = ARGB.color(alpha, (rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
        collector.order(1).submitModel(getParentModel(), state, poseStack, RenderTypes.entityTranslucent(renderer.getTextureLocation(state)), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor);
    }
}
