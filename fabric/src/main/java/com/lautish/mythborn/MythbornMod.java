package com.lautish.mythborn;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.mythborn.init.ModAttributes;
import com.lautish.mythborn.init.ModConfig;
import net.fabricmc.api.ModInitializer;

public class MythbornMod implements ModInitializer {

    public static final String MODID = "mythborn";

    @Override
    public void onInitialize() {
        TXFConfig.init(MODID, ModConfig.class);
        ModAttributes.init();
    }
}
