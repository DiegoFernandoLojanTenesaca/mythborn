package com.lautish.mythborn;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.mythborn.init.ModAttributes;
import com.lautish.mythborn.init.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MythbornMod.MODID)
public class MythbornMod {

    public static final String MODID = "mythborn";

    public MythbornMod(IEventBus modBus) {
        TXFConfig.init(MODID, ModConfig.class);
        ModAttributes.init(modBus);
    }
}
