package com.lautish.luckymobs;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.luckymobs.init.ModConfig;
import net.fabricmc.api.ModInitializer;

public class LuckyMobsMod implements ModInitializer {

    public static final String MODID = "luckymobs";

    @Override
    public void onInitialize() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
