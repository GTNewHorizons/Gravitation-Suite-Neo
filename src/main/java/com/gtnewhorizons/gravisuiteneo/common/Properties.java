package com.gtnewhorizons.gravisuiteneo.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class Properties {

    public static final int GUIID_ADVDRILL = 4;
    public static final int GUIID_PLASMALAUNCHER = 5;

    public static boolean disableBasicRecipes;
    public static boolean disableEpicLappackRecipe;
    public static boolean disableNanobotRecipe;

    private static final String ADDITIONAL_TWEAKS = "additional tweaks";
    private static final String ARMOR_PRESETS = "armor presets";
    private static final String ELECTRIC_PRESETS = "electric presets";
    private static final String HUD_SETTINGS = "hud settings";
    private static final String RECIPES_SETTINGS = "recipes settings";
    private static final String RELOCATOR_SETTINGS = "relocator settings";
    private static final String SOUNDS_SETTINGS = "sounds settings";
    private static final String VAJRA_SETTINGS = "vajra settings";

    private static Configuration config;

    public static void init(Configuration config) {
        Properties.config = config;

        disableBasicRecipes = config.getBoolean("Disable Basic recipes", "Recipes settings", false, "");
        disableEpicLappackRecipe = config.getBoolean("Disable EpicLappack recipe", "Recipes settings", false, "");
        disableNanobotRecipe = config.getBoolean("Disable Nanobot recipe", "Recipes settings", false, "");

        for (ArmorPresets ap : ArmorPresets.values()) {
            ap.update();
        }
        for (ElectricPresets ep : ElectricPresets.values()) {
            ep.update();
        }

        AdvTweaks.init();
        addPropertyComments();
    }

    private static void addPropertyComments() {
        setComment(HUD_SETTINGS, "Display hud", "[default: true]");
        setComment(HUD_SETTINGS, "hudPosition", "[range: 1 ~ 4, default: 1]");

        setComment(RECIPES_SETTINGS, "Disable Advanced Chainsaw recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable Advanced Dimond Drill recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable Advanced Jetpack recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable Advanced NanoChestPlate recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable AdvancedLappack recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable GraviChestPlate recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable GraviTool recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable Relocator recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable UltimateLappack recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable UltimateSolarHelmet recipe", "[default: false]");
        setComment(RECIPES_SETTINGS, "Disable Vajra recipe", "[default: false]");

        setComment(RELOCATOR_SETTINGS, "Disable portal mode", "[default: false]");
        setComment(RELOCATOR_SETTINGS, "Disable translocator mode", "[default: false]");
        setComment(RELOCATOR_SETTINGS, "relocatorEnergyPerDimesionTp", "[range: 0 ~ 2147483647, default: 1500000]");
        setComment(RELOCATOR_SETTINGS, "relocatorEnergyPerPortal", "[range: 0 ~ 2147483647, default: 2500000]");
        setComment(RELOCATOR_SETTINGS, "relocatorEnergyPerStandartTp", "[range: 0 ~ 2147483647, default: 1000000]");
        setComment(RELOCATOR_SETTINGS, "relocatorEnergyPerTranslocator", "[range: 0 ~ 2147483647, default: 2000000]");

        setComment(SOUNDS_SETTINGS, "Disable all sounds", "[default: false]");

        setComment(VAJRA_SETTINGS, "Disable Vajra accurate mode", "[default: false]");
    }

    private static void setComment(String categoryKey, String propertyKey, String comment) {
        config.getCategory(categoryKey).get(propertyKey).comment = comment;
    }

    public enum ArmorPresets {

        AdvJetPack(0.0),
        AdvLapPack(0.0),
        AdvNanoChestPlate(0.36),
        GraviChestPlate(0.44);

        public double absorptionRatio;

        ArmorPresets(double absorptionRatio) {
            this.absorptionRatio = absorptionRatio;
        }

        private void update() {
            final String category = ARMOR_PRESETS + Configuration.CATEGORY_SPLITTER + this.name();

            this.absorptionRatio = config.get(
                    category,
                    "absorptionRatio",
                    this.absorptionRatio,
                    "How much damage this armor pieve can absorb [range: 0.0 ~ 1.0, default: " + this.absorptionRatio
                            + "]",
                    0.0,
                    1.0).getDouble();
        }
    }

    public enum ElectricPresets {

        AdvJetPack(3000000, 3, 3000, 12),
        AdvChainsaw(1000000, 2, 500, 100),
        AdvDrill(10000000, 4, 100000, 3333),
        GraviChestPlate(30000000, 4, 300000, 278),
        GraviTool(300000, 2, 3000, 500),
        Relocator(10000000, 4, 100000, 0),
        AdvLapPack(3000000, 3, 30000, 0),
        UltimateLappack(30000000, 4, 300000, 0),
        EpicLappack(300000000, 5, 3000000, 0),
        Vajra(10000000, 4, 100000, 3333),
        PlasmaLauncher(10000000, 5, 375000, 1000000),
        PlasmaCell(100000000, 5, 375000, 0);

        public int maxCharge;
        public int transferLimit;
        public int tier;
        public int energyPerOperation;

        ElectricPresets(int maxCharge, int tier, int transferLimit, int energyPerOperation) {
            this.maxCharge = maxCharge;
            this.transferLimit = transferLimit;
            this.tier = tier;
            this.energyPerOperation = energyPerOperation;
        }

        private void update() {
            final String category = ELECTRIC_PRESETS + Configuration.CATEGORY_SPLITTER + this.name();

            this.energyPerOperation = config.getInt(
                    "EnergyPerOperation",
                    category,
                    this.energyPerOperation,
                    0,
                    Integer.MAX_VALUE,
                    "How many Energy Units this item will consume per operation (items with multiple modes may consume a different amount)");

            this.maxCharge = config.getInt(
                    "MaxCharge",
                    category,
                    this.maxCharge,
                    0,
                    Integer.MAX_VALUE,
                    "How many Energy Units this item is able to store");

            this.tier = config.getInt("Tier", category, this.tier, 0, 14, "This item's electric tier");

            this.transferLimit = config.getInt(
                    "TransferLimit",
                    category,
                    this.transferLimit,
                    0,
                    Integer.MAX_VALUE,
                    "How many Energy Units can be stored in/retrieved from this item per transfer");
        }
    }

    public static class AdvTweaks {

        private static int maxMiningLevel;
        private static int epicLappackChargeTickChance;
        private static String[] plasmaLauncherFluids;
        private static String[] advChainsawAdditionalMineableBlocks;
        private static Collection<UniqueIdentifier> allowedShieldHelmets;
        private static Collection<UniqueIdentifier> allowedShieldLeggins;
        private static Collection<UniqueIdentifier> allowedShieldBoots;

        public static int getMaxMiningLevel() {
            return maxMiningLevel;
        }

        public static int getEpicLappackChargeTickChance() {
            return epicLappackChargeTickChance;
        }

        public static String[] getPlasmaLauncherFluids() {
            return plasmaLauncherFluids;
        }

        public static String[] getAdvChainsawAdditionalMineableBlocks() {
            return advChainsawAdditionalMineableBlocks;
        }

        public static Collection<UniqueIdentifier> getAllowedShieldHelmets() {
            return allowedShieldHelmets;
        }

        public static Collection<UniqueIdentifier> getAllowedShieldLeggins() {
            return allowedShieldLeggins;
        }

        public static Collection<UniqueIdentifier> getAllowedShieldBoots() {
            return allowedShieldBoots;
        }

        private static void init() {
            maxMiningLevel = config.getInt(
                    "MaxToolLevelAvailable",
                    ADDITIONAL_TWEAKS,
                    3,
                    0,
                    999,
                    "Set the maximum tool-level here. Only required if you have Tinkers/Iguana and want pretty tooltips");

            epicLappackChargeTickChance = config.getInt(
                    "EpicLapPackChargeTickChance",
                    ADDITIONAL_TWEAKS,
                    10,
                    1,
                    999,
                    "The chance each tick that the epic lappack will scan a players inventory for chargeable items. 1 means it will scan every tick and thus charge a lot quicker. 20 is about each second");

            plasmaLauncherFluids = config.getStringList(
                    "PlasmaLauncherFluids",
                    ADDITIONAL_TWEAKS,
                    new String[0],
                    "List of fluids usable in the Plasma Launcher. Formatting: fluidName:damageMultiplier");

            advChainsawAdditionalMineableBlocks = config.getStringList(
                    "AdvChainsawAdditionalMineableBlocks",
                    ADDITIONAL_TWEAKS,
                    new String[] { "minecraft:leaves", "minecraft:wool", "minecraft:melon_block", "minecraft:cactus",
                            "minecraft:snow", "IC2:blockRubLeaves" },
                    "List of blocks the Advanced Chainsaw should be able to break. (Note: The Advanced Chainsaw can also break the same blocks as Diamond Axe and Diamond Sword)");

            allowedShieldHelmets = Arrays.stream(
                    config.getStringList(
                            "AllowedShieldHelmets",
                            ADDITIONAL_TWEAKS,
                            new String[] { "AdvancedSolarPanel:hybrid_solar_helmet",
                                    "AdvancedSolarPanel:ultimate_solar_helmet", "EMT:QuantumGogglesRevealing",
                                    "EMT:SolarHelmetRevealing" },
                            "These items can be used in place of the Quantum Helmet and still allow the usage of the Quantum Shield"))
                    .map(UniqueIdentifier::new).collect(Collectors.toCollection(HashSet::new));

            allowedShieldLeggins = Arrays.stream(
                    config.getStringList(
                            "AllowedShieldLeggins",
                            ADDITIONAL_TWEAKS,
                            new String[0],
                            "These items can be used in place of the Quantum Leggins and still allow the usage of the Quantum Shield"))
                    .map(UniqueIdentifier::new).collect(Collectors.toCollection(HashSet::new));

            allowedShieldBoots = Arrays.stream(
                    config.getStringList(
                            "AllowedShieldBoots",
                            ADDITIONAL_TWEAKS,
                            new String[] { "EMT:QuantumBootsTraveller" },
                            "These items can be used in place of the Quantum Boots and still allow the usage of the Quantum Shield"))
                    .map(UniqueIdentifier::new).collect(Collectors.toCollection(HashSet::new));
        }
    }
}
