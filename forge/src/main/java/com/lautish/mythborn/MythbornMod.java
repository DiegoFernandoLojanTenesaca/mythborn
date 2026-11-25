package com.lautish.mythborn;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.mythborn.init.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MythbornMod.MODID)
public class MythbornMod {

    public static final String MODID = "mythborn";

    public MythbornMod(FMLJavaModLoadingContext context) {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
