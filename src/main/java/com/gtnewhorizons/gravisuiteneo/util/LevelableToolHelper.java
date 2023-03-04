package com.gtnewhorizons.gravisuiteneo.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.gravisuiteneo.common.Properties;

import gravisuite.GraviSuite;
import gravisuite.ServerProxy;

public class LevelableToolHelper {

    public static void AddXP(EntityPlayer player, ItemStack itemStack, int amount) {
        double currentXP = readToolXP(itemStack);
        int currLevel = getLevel(currentXP);

        if (Double.MAX_VALUE - currentXP >= amount) {
            currentXP += amount;
        }

        int nextLevel = getLevel(currentXP);
        if (nextLevel > currLevel) {
            ServerProxy.sendPlayerMessage(
                    player,
                    EnumChatFormatting.GOLD + StatCollector.translateToLocal("message.xp.levelup"));
        }

        saveToolXP(itemStack, currentXP);
    }

    public static int getLevel(double xpValue) {
        return (int) Math.floor(Math.sqrt(xpValue / Properties.AdvTweaks.getXpGainFactor()));
    }

    public static int getLevel(ItemStack itemStack) {
        return getLevel(readToolXP(itemStack));
    }

    public static boolean hasLevel(ItemStack itemStack, int level) {
        return getLevel(itemStack) >= level;
    }

    public static int getXPForLevel(int level) {
        return Properties.AdvTweaks.getXpGainFactor() * level * level;
    }

    public static double readToolXP(ItemStack itemStack) {
        final NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemStack);
        return nbttagcompound.getDouble("toolXP");
    }

    public static void saveToolXP(ItemStack itemStack, double toolXP) {
        final NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemStack);
        nbttagcompound.setDouble("toolXP", toolXP);
    }
}
