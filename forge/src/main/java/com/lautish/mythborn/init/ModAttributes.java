package com.lautish.mythborn.init;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;

import java.util.List;

public class ModAttributes {
    private static final List<Holder<Attribute>> COMBAT = List.of(Attributes.ATTACK_DAMAGE, Attributes.ATTACK_KNOCKBACK);

    public static void init() {
        EntityAttributeModificationEvent.BUS.addListener(ModAttributes::add);
    }

    private static void add(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            if (type.getCategory() == MobCategory.MONSTER) continue;
            for (Holder<Attribute> attribute : COMBAT) {
                if (!event.has(type, attribute)) event.add(type, attribute, 0.0);
            }
        }
    }
}
