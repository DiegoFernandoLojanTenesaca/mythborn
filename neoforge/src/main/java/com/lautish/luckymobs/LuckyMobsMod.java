package com.lautish.luckymobs;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.luckymobs.init.ModConfig;
import net.neoforged.fml.common.Mod;

@Mod(LuckyMobsMod.MODID)
public class LuckyMobsMod {

    public static final String MODID = "luckymobs";

    public LuckyMobsMod() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
