package com.lautish.alphamobs;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.alphamobs.init.ModConfig;
import net.fabricmc.api.ModInitializer;

public class AlphaMobsMod implements ModInitializer {

    public static final String MODID = "alphamobs";

    @Override
    public void onInitialize() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
