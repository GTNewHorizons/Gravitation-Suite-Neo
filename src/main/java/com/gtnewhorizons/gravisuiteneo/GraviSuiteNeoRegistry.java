package com.gtnewhorizons.gravisuiteneo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.items.ItemEpicLappack;
import com.gtnewhorizons.gravisuiteneo.items.ItemPlasmaCell;
import com.gtnewhorizons.gravisuiteneo.items.ItemPlasmaLauncher;
import com.gtnewhorizons.gravisuiteneo.util.GregTechAPIHelper;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import gravisuite.GraviSuite;

public class GraviSuiteNeoRegistry {

    public static Item plasmaLauncher;
    public static Item epicLappack;
    public static Item itemPlasmaCell;
    public static CreativeTabs graviCreativeTab;
    private static Map<Fluid, Float> fuelMap;

    private GraviSuiteNeoRegistry() {}

    public static void register() {
        graviCreativeTab = new CreativeTabs("GraviSuite") {

            @Override
            public Item getTabIconItem() {
                return GraviSuite.graviChestPlate;
            }
        };
        plasmaLauncher = new ItemPlasmaLauncher();
        epicLappack = new ItemEpicLappack();
        itemPlasmaCell = new ItemPlasmaCell();
        GameRegistry.registerItem(plasmaLauncher, "sonicLauncher");
        GameRegistry.registerItem(epicLappack, "epicLappack");
        GameRegistry.registerItem(itemPlasmaCell, "itemPlasmaCell");
    }

    /**
     * Get the efficiency-value in percent for given fluidstack
     */
    public static float getPlasmaEfficiency(FluidStack plasmaStack) {
        if (fuelMap == null) {
            initFuelmap();
        }
        return fuelMap.getOrDefault(plasmaStack.getFluid(), 0.0f);
    }

    public static Set<Fluid> getRegisteredFuels() {
        return Collections.unmodifiableSet(fuelMap.keySet());
    }

    /**
     * Init the fuelmap that is required to lookup efficiency later
     */
    @SuppressWarnings("deprecation")
    private static void initFuelmap() {
        fuelMap = new HashMap<>();
        // Get the median of all PlasmaFuels
        Map<Fluid, Integer> fluids = new HashMap<>();
        for (String entry : Properties.AdvTweaks.getPlasmaLauncherFluids()) {
            String[] splitEntry = entry.split(":", 1);
            if (splitEntry.length != 2) {
                continue;
            }
            int fuelValue;
            try {
                fuelValue = Integer.parseInt(splitEntry[1]);
            } catch (Exception e) {
                continue;
            }
            Fluid fluid = FluidRegistry.getFluid(splitEntry[0]);
            if (fluid == null) {
                continue;
            }
            fluids.put(fluid, fuelValue);
        }
        if (Loader.isModLoaded("gregtech")) {
            fluids.putAll(GregTechAPIHelper.getGTPlasmaFluids());
        }
        long sum = 0;
        for (int fuelValue : fluids.values()) {
            sum += fuelValue;
        }
        if (sum == 0) {
            return;
        } else {
            fluids.size();
        }

        // Get the percentage-multiplier for our median value, then loop the fuels again and prepare a static list
        // For all available plasma fuels
        float plasmaMedianMultip = 100.0f / sum * fluids.size();
        GraviSuiteNeo.LOGGER.debug("Median Plasma Multiplier: {}", plasmaMedianMultip);
        for (Entry<Fluid, Integer> entry : fluids.entrySet()) {
            Fluid fluid = entry.getKey();
            float fuelValue = entry.getValue() * plasmaMedianMultip;
            fuelMap.put(fluid, fuelValue);
            GraviSuiteNeo.LOGGER.debug("Added Fluid {} with Fuel Value {}", fluid.getLocalizedName(), fuelValue);
        }
    }
}
