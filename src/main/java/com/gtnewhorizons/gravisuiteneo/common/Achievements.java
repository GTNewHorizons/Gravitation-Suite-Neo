package com.gtnewhorizons.gravisuiteneo.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;

import gravisuite.GraviSuite;

public class Achievements {

    public static final Achievement PLASMAGUN;
    public static final Achievement OVER9000;
    public static final Achievement ULTRAKILL;
    public static final Achievement VAPORIZE_SELF;
    public static final Achievement POWERDRILL;
    public static final Achievement POWERDRILL_MARKIII;
    public static final Achievement QSHIELD;
    public static final Achievement QSHIELD_PLASMAIMPACT;
    public static final Achievement EPIC_LAPPACK;

    static {
        // Plasma Cannon tree
        PLASMAGUN = create("gravisuite.craft_plasma", GraviSuiteNeoRegistry.plasmaLauncher, -4, -2);
        OVER9000 = createSpecial("gravisuite.itsover9000", new ItemStack(Items.skull, 1, 1), -4, -4, PLASMAGUN);
        ULTRAKILL = createSpecial("gravisuite.ultrakill", new ItemStack(Items.skull, 1, 2), -6, -2, PLASMAGUN);
        VAPORIZE_SELF = create("gravisuite.vaporizeself", Items.skull, -4, 0, PLASMAGUN);

        // Power Drill tree
        POWERDRILL = create("gravisuite.powerdrill_craft", GraviSuite.advDDrill, -2, -2);
        POWERDRILL_MARKIII = createSpecial(
                "gravisuite.powerdrill_markIII",
                withEffect(GraviSuite.advDDrill),
                -2,
                0,
                POWERDRILL);

        // Quantum Shield tree
        QSHIELD = create("gravisuite.qshield_enable", GraviSuite.graviChestPlate, 0, -2);
        QSHIELD_PLASMAIMPACT = createSpecial(
                "gravisuite.qshield_plasmaimpact",
                GraviSuiteNeoRegistry.itemPlasmaCell,
                0,
                0,
                QSHIELD);

        // Epic Lappack tree
        EPIC_LAPPACK = createSpecial("gravisuite.epic_lappack", GraviSuiteNeoRegistry.epicLappack, 2, -2);
    }

    public static void registerAchievementPage() {
        AchievementPage.registerAchievementPage(
                new AchievementPage(
                        StatCollector.translateToLocal("gravisuite.achievementPage.name"),
                        PLASMAGUN,
                        OVER9000,
                        ULTRAKILL,
                        VAPORIZE_SELF,
                        POWERDRILL,
                        POWERDRILL_MARKIII,
                        QSHIELD,
                        QSHIELD_PLASMAIMPACT,
                        EPIC_LAPPACK));
    }

    private static Achievement create(String name, Item item, int posX, int posY) {
        return new Achievement(name, name, posX, posY, item, null).registerStat().initIndependentStat();
    }

    private static Achievement create(String name, Item item, int posX, int posY, Achievement preReq) {
        return new Achievement(name, name, posX, posY, item, preReq).registerStat();
    }

    private static Achievement createSpecial(String name, Item item, int posX, int posY) {
        return new Achievement(name, name, posX, posY, item, null).registerStat().initIndependentStat().setSpecial();
    }

    private static Achievement createSpecial(String name, Item item, int posX, int posY, Achievement preReq) {
        return new Achievement(name, name, posX, posY, item, preReq).registerStat().setSpecial();
    }

    private static Achievement createSpecial(String name, ItemStack item, int posX, int posY, Achievement preReq) {
        return new Achievement(name, name, posX, posY, item, preReq).registerStat().setSpecial();
    }

    private static ItemStack withEffect(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.addEnchantment(Enchantment.protection, 1);
        return stack;
    }

    private Achievements() {}
}
