package com.lautish.luckymobs;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.luckymobs.init.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LuckyMobsMod.MODID)
public class LuckyMobsMod {

    public static final String MODID = "luckymobs";

    public LuckyMobsMod(FMLJavaModLoadingContext context) {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
