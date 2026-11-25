package com.lautish.alphamobs.init.mixin;

import com.lautish.alphamobs.display.LuckyRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements LuckyRenderState {
    @Unique
    private int veil = -1;

    @Override
    public int veil() {
        return veil;
    }

    @Override
    public void setVeil(int veil) {
        this.veil = veil;
    }
}
