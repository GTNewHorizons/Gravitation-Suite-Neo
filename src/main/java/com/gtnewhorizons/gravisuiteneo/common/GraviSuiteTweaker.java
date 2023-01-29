package com.gtnewhorizons.gravisuiteneo.common;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;

import cpw.mods.fml.relauncher.ReflectionHelper;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvChainsaw;
import gravisuite.ItemAdvDDrill;
import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ItemGraviTool;
import gravisuite.ItemRelocator;
import gravisuite.ItemSimpleItems;
import gravisuite.ItemVajra;

public class GraviSuiteTweaker {

    private GraviSuiteTweaker() {}

    public static void tweak() {
        tweakCreativeTabs();
        tweakElectricProperties();

        ItemSimpleItems.itemNames.add("itemAntidote");
    }

    private static void tweakCreativeTabs() {
        GraviSuite.instance.itemSimpleItem.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.advChainsaw.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.advDDrill.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.advJetpack.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.advLappack.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.advNanoChestPlate.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.graviChestPlate.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.graviTool.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.relocator.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.sonicLauncher.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        GraviSuite.ultimateLappack.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
    }

    private static void tweakElectricProperties() {
        ReflectionHelper.setPrivateValue(
                ItemAdvChainsaw.class,
                (ItemAdvChainsaw) GraviSuite.advChainsaw,
                Properties.ElectricPresets.AdvChainsaw.energyPerOperation,
                "energyPerOperation");
        ReflectionHelper.setPrivateValue(
                ItemAdvChainsaw.class,
                (ItemAdvChainsaw) GraviSuite.advChainsaw,
                Properties.ElectricPresets.AdvChainsaw.maxCharge,
                "maxCharge");
        ReflectionHelper.setPrivateValue(
                ItemAdvChainsaw.class,
                (ItemAdvChainsaw) GraviSuite.advChainsaw,
                Properties.ElectricPresets.AdvChainsaw.tier,
                "tier");
        ReflectionHelper.setPrivateValue(
                ItemAdvChainsaw.class,
                (ItemAdvChainsaw) GraviSuite.advChainsaw,
                Properties.ElectricPresets.AdvChainsaw.transferLimit,
                "transferLimit");

        ReflectionHelper.setPrivateValue(
                ItemAdvDDrill.class,
                (ItemAdvDDrill) GraviSuite.advDDrill,
                Properties.ElectricPresets.AdvDrill.energyPerOperation,
                "energyPerOperation");
        ReflectionHelper.setPrivateValue(
                ItemAdvDDrill.class,
                (ItemAdvDDrill) GraviSuite.advDDrill,
                Properties.ElectricPresets.AdvDrill.energyPerOperation / 2,
                "energyPerLowOperation");
        ReflectionHelper.setPrivateValue(
                ItemAdvDDrill.class,
                (ItemAdvDDrill) GraviSuite.advDDrill,
                Properties.ElectricPresets.AdvDrill.energyPerOperation / 4,
                "energyPerUltraLowOperation");
        ReflectionHelper.setPrivateValue(
                ItemAdvDDrill.class,
                (ItemAdvDDrill) GraviSuite.advDDrill,
                Properties.ElectricPresets.AdvDrill.maxCharge,
                "maxCharge");
        ReflectionHelper.setPrivateValue(
                ItemAdvDDrill.class,
                (ItemAdvDDrill) GraviSuite.advDDrill,
                Properties.ElectricPresets.AdvDrill.tier,
                "tier");
        ReflectionHelper.setPrivateValue(
                ItemAdvDDrill.class,
                (ItemAdvDDrill) GraviSuite.advDDrill,
                Properties.ElectricPresets.AdvDrill.transferLimit,
                "transferLimit");

        ItemAdvancedJetPack.energyPerTick = Properties.ElectricPresets.AdvJetPack.energyPerOperation;
        ItemAdvancedJetPack.maxCharge = Properties.ElectricPresets.AdvJetPack.maxCharge;
        ReflectionHelper.setPrivateValue(
                ItemAdvancedJetPack.class,
                (ItemAdvancedJetPack) GraviSuite.advJetpack,
                Properties.ElectricPresets.AdvJetPack.tier,
                "tier");
        ReflectionHelper.setPrivateValue(
                ItemAdvancedJetPack.class,
                (ItemAdvancedJetPack) GraviSuite.advJetpack,
                Properties.ElectricPresets.AdvJetPack.transferLimit,
                "transferLimit");

        ItemGraviChestPlate.dischargeInFlight = Properties.ElectricPresets.GraviChestPlate.energyPerOperation;
        ItemGraviChestPlate.maxCharge = Properties.ElectricPresets.GraviChestPlate.maxCharge;
        ItemGraviChestPlate.tier = Properties.ElectricPresets.GraviChestPlate.tier;
        ItemGraviChestPlate.transferLimit = Properties.ElectricPresets.GraviChestPlate.transferLimit;

        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.energyPerOperation / 10,
                "energyPerHoe");
        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.energyPerOperation / 10,
                "energyPerTreeTap");
        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.energyPerOperation / 10,
                "energyPerSwitchSide");
        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.energyPerOperation,
                "energyPerWrenchStandartOperation");
        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.energyPerOperation * 20,
                "energyPerWrenchFineOperation");
        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.maxCharge,
                "maxCharge");
        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.tier,
                "tier");
        ReflectionHelper.setPrivateValue(
                ItemGraviTool.class,
                (ItemGraviTool) GraviSuite.graviTool,
                Properties.ElectricPresets.GraviTool.transferLimit,
                "transferLimit");

        ReflectionHelper.setPrivateValue(
                ItemRelocator.class,
                (ItemRelocator) GraviSuite.relocator,
                (double) Properties.ElectricPresets.Relocator.maxCharge,
                "maxCharge");
        ReflectionHelper.setPrivateValue(
                ItemRelocator.class,
                (ItemRelocator) GraviSuite.relocator,
                Properties.ElectricPresets.Relocator.tier,
                "tier");
        ReflectionHelper.setPrivateValue(
                ItemRelocator.class,
                (ItemRelocator) GraviSuite.relocator,
                Properties.ElectricPresets.Relocator.transferLimit,
                "transferLimit");

        ReflectionHelper.setPrivateValue(
                ItemVajra.class,
                (ItemVajra) GraviSuite.vajra,
                Properties.ElectricPresets.Vajra.energyPerOperation,
                "energyPerOperation");
        ReflectionHelper.setPrivateValue(
                ItemVajra.class,
                (ItemVajra) GraviSuite.vajra,
                Properties.ElectricPresets.Vajra.maxCharge,
                "maxCharge");
        ReflectionHelper.setPrivateValue(
                ItemVajra.class,
                (ItemVajra) GraviSuite.vajra,
                Properties.ElectricPresets.Vajra.tier,
                "tier");
        ReflectionHelper.setPrivateValue(
                ItemVajra.class,
                (ItemVajra) GraviSuite.vajra,
                Properties.ElectricPresets.Vajra.transferLimit,
                "transferLimit");
    }
}
