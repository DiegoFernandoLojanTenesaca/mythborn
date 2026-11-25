package com.lautish.mythborn.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import static com.lautish.mythborn.MythbornMod.MODID;

public class ModTags {
    public interface EntityTypes {
        TagKey<EntityType<?>> CAN_BE_LUCKY = create("can_be_lucky");
        TagKey<EntityType<?>> CANNOT_BE_LUCKY = create("cannot_be_lucky");
        TagKey<EntityType<?>> LUCKY_MOUNTS = create("lucky_mounts");

        private static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MODID, name));
        }
    }
}
