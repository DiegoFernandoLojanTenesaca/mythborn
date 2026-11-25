package com.lautish.alphamobs;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.alphamobs.init.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AlphaMobsMod.MODID)
public class AlphaMobsMod {

    public static final String MODID = "alphamobs";

    public AlphaMobsMod(FMLJavaModLoadingContext context) {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
