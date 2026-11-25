package com.lautish.mythborn;

import com.jahirtrap.configlib.TXFConfig;
import com.lautish.mythborn.init.ModConfig;
import net.neoforged.fml.common.Mod;

@Mod(MythbornMod.MODID)
public class MythbornMod {

    public static final String MODID = "mythborn";

    public MythbornMod() {
        TXFConfig.init(MODID, ModConfig.class);
    }
}
