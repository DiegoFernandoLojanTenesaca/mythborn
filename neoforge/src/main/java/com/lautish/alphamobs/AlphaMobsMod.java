package com.lautish.alphamobs;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.alphamobs.init.ModConfig;
import net.neoforged.fml.common.Mod;

@Mod(AlphaMobsMod.MODID)
public class AlphaMobsMod {

    public static final String MODID = "alphamobs";

    public AlphaMobsMod() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
