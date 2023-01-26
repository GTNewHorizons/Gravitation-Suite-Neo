package com.gtnewhorizons.gravisuiteneo;

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
import net.minecraftforge.common.MinecraftForge;

@Mod(
        acceptedMinecraftVersions = "[1.7.10]",
        dependencies =
                "required-after:GraviSuite@[1.7.10-2.0.3];required-after:IC2;required-after:gtnhmixins;after:gregtech",
        modid = GraviSuiteNeo.MODID,
        name = GraviSuiteNeo.MODNAME,
        version = GraviSuiteNeo.VERSION)
public class GraviSuiteNeo {

    public static final String MODID = "gravisuiteneo";
    public static final String MODNAME = "Gravitation Suite Neo";
    public static final String VERSION = "GRADLETOKEN_VERSION";

    @SidedProxy(
            clientSide = "com.gtnewhorizons.gravisuiteneo.proxy.ClientProxy",
            serverSide = "com.gtnewhorizons.gravisuiteneo.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // register stuff
        GraviSuiteNeoRegistry.register();
        proxy.registerRenderers();
        proxy.registerKeys();

        // tweak stuff
        GraviSuiteTweaker.tweak();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        EventHandler eventHandler = new EventHandler();
        FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);
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
