package com.gtnewhorizons.gravisuiteneo;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizons.gravisuiteneo.common.Achievements;
import com.gtnewhorizons.gravisuiteneo.common.EventHandler;
import com.gtnewhorizons.gravisuiteneo.common.GraviSuiteTweaker;
import com.gtnewhorizons.gravisuiteneo.common.RecipeHandler;
import com.gtnewhorizons.gravisuiteneo.proxy.CommonProxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.Type;

@Mod(
        acceptedMinecraftVersions = "[1.7.10]",
        dependencies = "required-after:GraviSuite@[1.7.10-2.0.3];required-after:IC2;after:gregtech",
        modid = GraviSuiteNeo.MODID,
        name = GraviSuiteNeo.MODNAME,
        version = GraviSuiteNeo.VERSION)
public class GraviSuiteNeo {

    public static final String MODID = "gravisuiteneo";
    public static final String MODNAME = "Gravitation Suite Neo";
    public static final String VERSION = Tags.VERSION;

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @SidedProxy(
            clientSide = "com.gtnewhorizons.gravisuiteneo.proxy.ClientProxy",
            serverSide = "com.gtnewhorizons.gravisuiteneo.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GraviSuiteNeoRegistry.register();
        proxy.registerRenderers();
        proxy.registerKeys();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        EventHandler eventHandler = new EventHandler();
        FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);
        GraviSuiteTweaker.tweak();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Achievements.registerAchievementPage();
        RecipeHandler.registerRecipes();
    }

    @Mod.EventHandler
    public void remap(FMLMissingMappingsEvent event) {
        for (MissingMapping missingMapping : event.getAll()) {
            if (missingMapping.type != Type.ITEM) continue;
            switch (missingMapping.name) {
                case "GraviSuite:epicLappack":
                    missingMapping.remap(GraviSuiteNeoRegistry.epicLappack);
                    break;
                case "GraviSuite:itemPlasmaCell":
                    missingMapping.remap(GraviSuiteNeoRegistry.itemPlasmaCell);
                    break;
                case "GraviSuite:sonicLauncher":
                    missingMapping.remap(GraviSuiteNeoRegistry.plasmaLauncher);
                    break;
                default:
                    break;
            }
        }
    }
}
