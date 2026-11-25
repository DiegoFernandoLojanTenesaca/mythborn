package com.lautish.mythborn.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import java.util.List;

public class ModAttributes {
    private static final List<Holder<Attribute>> COMBAT = List.of(Attributes.ATTACK_DAMAGE, Attributes.ATTACK_KNOCKBACK);

    public static void init() {
        FabricDefaultAttributeRegistry.MODIFY.register(context -> context.modifyAll(ModAttributes::add));
    }

    private static void add(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        if (type.getCategory() == MobCategory.MONSTER) return;
        AttributeSupplier supplier = DefaultAttributes.getSupplier(type);
        for (Holder<Attribute> attribute : COMBAT) {
            if (!supplier.hasAttribute(attribute)) builder.add(attribute, 0.0);
        }
    }
}
