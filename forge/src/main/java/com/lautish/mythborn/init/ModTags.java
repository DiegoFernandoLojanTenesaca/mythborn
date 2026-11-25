package com.lautish.mythborn.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import static com.lautish.mythborn.MythbornMod.MODID;

public class ModTags {
    public interface EntityTypes {
        TagKey<EntityType<?>> CAN_BE_LUCKY = create("can_be_lucky");
        TagKey<EntityType<?>> CANNOT_BE_LUCKY = create("cannot_be_lucky");
        TagKey<EntityType<?>> LUCKY_MOUNTS = create("lucky_mounts");
        TagKey<EntityType<?>> BOSSES = convention("bosses");

        private static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MODID, name));
        }

        private static TagKey<EntityType<?>> convention(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", name));
        }
    }

    public interface Items {
        TagKey<Item> HIGH_GRADE_ARMOR = create("high_grade_armor");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, name));
        }
    }
}
