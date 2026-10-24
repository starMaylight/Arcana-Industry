package com.starmaylight.arcana_industry;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Arcana_industry.MODID)
public class Arcana_industry {
    public static final String MODID = "arcana_industry";
    public Arcana_industry() {
        com.starmaylight.arcana_industry.guide.GuideNetwork.register();
        MachineRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        com.starmaylight.arcana_industry.mob.MobRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        com.starmaylight.arcana_industry.ore.OreRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        com.starmaylight.arcana_industry.sound.ModSounds.register(FMLJavaModLoadingContext.get().getModEventBus());
        com.starmaylight.arcana_industry.magic.enchant.WandTunings.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
