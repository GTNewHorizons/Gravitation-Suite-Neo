package com.gtnewhorizons.gravisuiteneo.common;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import gravisuite.GraviSuite;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;

public enum Achievements {
    PLASMAGUN("gravisuite.craft_plasma", Items.skull, -4, -2),
    OVER9000("gravisuite.itsover9000", Items.nether_star, -4, -4, true, Achievements.PLASMAGUN),
    ULTRAKILL("gravisuite.ultrakill", Items.nether_star, -6, -2, true, Achievements.PLASMAGUN),
    VAPORIZE_SELF("gravisuite.vaporizeself", Items.skull, -4, 0, true, Achievements.PLASMAGUN),
    POWERDRILL("gravisuite.powerdrill_craft", GraviSuite.advDDrill, -2, -2),
    POWERDRILL_MARKIII("gravisuite.powerdrill_markIII", GraviSuite.advDDrill, -2, 0, true, Achievements.POWERDRILL),
    QSHIELD("gravisuite.qshield_enable", GraviSuite.graviChestPlate, 0, -2),
    QSHIELD_PLASMAIMPACT(
            "gravisuite.qshield_plasmaimpact", GraviSuiteNeoRegistry.itemPlasmaCell, 0, 0, true, Achievements.QSHIELD),
    EPIC_LAPPACK("gravisuite.epic_lappack", GraviSuiteNeoRegistry.epicLappack, 2, -2, true);

    private final String name;
    private final Item item;
    private final Achievement achievement;

    private static final class StaticFields {
        private static AchievementPage achievementsPage;
        private static final HashMap<String, Achievement> ACHIEVEMENTS = new HashMap<>();
    }

    public Achievement getAchievement() {
        return this.achievement;
    }

    public String getName() {
        return this.name;
    }

    public Item getItem() {
        return this.item;
    }

    public void triggerAchievement(EntityPlayer player) {
        if (this.achievement != null && player != null) {
            player.triggerAchievement(this.achievement);
        }
    }

    Achievements(String name, Item item, int posX, int posY, boolean special, Achievements preReq) {
        this.name = name;
        this.item = item;
        if (StaticFields.ACHIEVEMENTS.containsKey(name)) {
            throw new IllegalArgumentException("You derp! Achievement %s already exists!");
        } else {
            this.achievement = new Achievement(
                            this.name,
                            this.name,
                            posX,
                            posY,
                            this.item,
                            preReq != null ? preReq.getAchievement() : null)
                    .registerStat();
            if (preReq == null) {
                this.achievement.initIndependentStat();
            }

            if (special) {
                this.achievement.setSpecial();
            }

            StaticFields.ACHIEVEMENTS.put(this.name, this.achievement);
        }
    }

    Achievements(String name, Item item, int posX, int posY, boolean special) {
        this(name, item, posX, posY, special, null);
    }

    Achievements(String name, Item item, int posX, int posY) {
        this(name, item, posX, posY, false, null);
    }

    public static void registerAchievementPage() {
        StaticFields.achievementsPage = new AchievementPage(
                StatCollector.translateToLocal("gravisuite.achievementPage.name"),
                StaticFields.ACHIEVEMENTS.values().toArray(new Achievement[0]));
        AchievementPage.registerAchievementPage(StaticFields.achievementsPage);
    }
}
