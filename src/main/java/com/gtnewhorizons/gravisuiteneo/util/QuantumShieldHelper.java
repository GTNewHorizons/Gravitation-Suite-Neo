package com.gtnewhorizons.gravisuiteneo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.common.Achievements;
import com.gtnewhorizons.gravisuiteneo.common.PacketQuantumShield;
import com.gtnewhorizons.gravisuiteneo.common.Properties;

import cpw.mods.fml.common.registry.GameRegistry;
import gravisuite.GraviSuite;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ItemSimpleItems;
import ic2.api.item.ElectricItem;
import ic2.core.item.armor.ItemArmorQuantumSuit;

public class QuantumShieldHelper {

    /** Maps player UUID -> HP and time */
    private static final Map<UUID, Pair<Float, Long>> PLAYER_HEALTH_MAP = new HashMap<>();

    private static final int DISCHARGE_CURATIVE = 4000;
    private static final int DISCHARGE_MEDKIT = 4000;
    public static final int DISCHARGE_EXTINGUISH = 1000;
    public static final int DISCHARGE_IDLE = 5000;

    /**
     * Check if the player has a full set of QuantumArmor + GraviChestplate
     */
    public static boolean hasValidShieldEquipment(EntityPlayer player) {
        ItemStack helmet = player.getEquipmentInSlot(4);
        ItemStack chest = player.getEquipmentInSlot(3);
        ItemStack leggings = player.getEquipmentInSlot(2);
        ItemStack boots = player.getEquipmentInSlot(1);

        return chest != null && chest.getItem() instanceof ItemGraviChestPlate
                && helmet != null
                && checkHelmetItem(helmet.getItem())
                && leggings != null
                && checkLegItem(leggings.getItem())
                && boots != null
                && checkBootsItem(boots.getItem());
    }

    private static boolean checkHelmetItem(Item item) {
        return item instanceof ItemArmorQuantumSuit
                || Properties.AdvTweaks.getAllowedShieldHelmets().contains(GameRegistry.findUniqueIdentifierFor(item));
    }

    private static boolean checkLegItem(Item item) {
        return item instanceof ItemArmorQuantumSuit
                || Properties.AdvTweaks.getAllowedShieldLeggins().contains(GameRegistry.findUniqueIdentifierFor(item));
    }

    private static boolean checkBootsItem(Item item) {
        return item instanceof ItemArmorQuantumSuit
                || Properties.AdvTweaks.getAllowedShieldBoots().contains(GameRegistry.findUniqueIdentifierFor(item));
    }

    /**
     * Switch shield mode on/off
     */
    public static void switchShieldMode(EntityPlayer player, ItemStack itemstack) {
        if (!hasValidShieldEquipment(player)) {
            player.addChatMessage(new ChatComponentTranslation("message.graviChestPlate.invalidshieldSetup")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            return;
        }

        if (!ElectricItem.manager.canUse(itemstack, DISCHARGE_IDLE * 10)) {
            player.addChatMessage(new ChatComponentTranslation("message.graviChestPlate.ShieldUpNotEnoughPower")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            return;
        }

        if (readShieldMode(itemstack)) {
            saveShieldMode(itemstack, false);
            player.addChatMessage(
                    new ChatComponentTranslation("message.graviChestPlate.shieldMode")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW))
                            .appendText(" ")
                            .appendSibling(new ChatComponentTranslation("message.text.disabled")
                                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))));
            notifyWorldShieldDown(player);
        } else {
            saveShieldMode(itemstack, true);
            player.addChatMessage(
                    new ChatComponentTranslation("message.graviChestPlate.shieldMode")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW))
                            .appendText(" ")
                            .appendSibling(new ChatComponentTranslation("message.text.enabled")
                                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
            notifyWorldShieldUp(player);
            player.triggerAchievement(Achievements.QSHIELD);
        }
    }

    private static void notifyWorldShieldUp(EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, GraviSuiteNeo.MODID + ":qshieldon", 1.25F, 1.0F);
        PacketQuantumShield.issue(player.getEntityId(), -4);
    }

    public static void notifyWorldShieldDown(EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, GraviSuiteNeo.MODID + ":qshieldoff", 1.25F, 1.0F);
        PacketQuantumShield.issue(player.getEntityId(), -5);
    }

    public static void runHealthMonitor(EntityPlayer player, ItemStack itemStack) {
        float currHealth = player.getHealth();
        Pair<Float, Long> lastHealthElement = null;
        UUID playerID = player.getUniqueID();
        if (PLAYER_HEALTH_MAP.containsKey(playerID)) {
            lastHealthElement = PLAYER_HEALTH_MAP.get(playerID);
        }

        if (lastHealthElement == null || lastHealthElement.getRight() - System.currentTimeMillis() < -5000L) {
            lastHealthElement = Pair.of(0.0F, System.currentTimeMillis());
        }

        if (currHealth > lastHealthElement.getLeft()) {
            PLAYER_HEALTH_MAP.put(playerID, Pair.of(currHealth, System.currentTimeMillis()));
        } else if (currHealth + 2.0F < lastHealthElement.getLeft()) {
            player.setHealth(lastHealthElement.getLeft());
            player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 40, 4));
            ElectricItem.manager.discharge(itemStack, 4000.0D, 4, false, false, false);
            player.worldObj.playSoundAtEntity(player, GraviSuiteNeo.MODID + ":qshieldimpact", 1.25F, 1.0F);
            PacketQuantumShield.issue(player.getEntityId(), -1);
            if (lastHealthElement.getRight() - System.currentTimeMillis() < -2000L) {
                PLAYER_HEALTH_MAP.put(playerID, Pair.of(currHealth, System.currentTimeMillis()));
            }
        }
    }

    public static void performMedkitAction(EntityPlayer player, ItemStack itemstack) {
        if (player == null || itemstack == null) {
            return;
        }

        if (!(itemstack.getItem() instanceof ItemGraviChestPlate)) {
            return;
        }

        if (ElectricItem.manager.canUse(itemstack, DISCHARGE_MEDKIT)) {
            boolean nanobotsUsed = false;

            for (int i = 0; i < 36; i++) {
                ItemStack is = player.inventory.mainInventory[i];
                if (is != null) {
                    if (is.getItem() instanceof ItemSimpleItems) {
                        if (is.getItemDamage() == 7) {
                            if (is.stackSize == 1) {
                                player.inventory.mainInventory[i] = null;
                            } else {
                                is.stackSize -= 1;
                            }
                            nanobotsUsed = true;
                            break;
                        }
                    }
                }
            }

            if (nanobotsUsed) {
                ElectricItem.manager.discharge(itemstack, DISCHARGE_MEDKIT, 4, true, false, false);
                curePotions(itemstack, player, true);
                // TODO: add medkit sound player.worldObj.playSoundAtEntity(player, GraviSuiteNeo.MODID + ":medkit",
                // 1.25F, 1.0F);
                player.addChatMessage(new ChatComponentTranslation("message.graviChestPlate.MedkitInjected")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            } else {
                player.addChatMessage(new ChatComponentTranslation("message.graviChestPlate.MedkitNoNanoBots")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            }
        } else {
            player.addChatMessage(new ChatComponentTranslation("message.graviChestPlate.MedkitNoPower")
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
        }
    }

    public static void curePotions(ItemStack itemstack, EntityPlayer player, boolean medkitCure) {
        List<Potion> potions = new ArrayList<>();
        potions.add(Potion.wither);

        if (medkitCure) {
            potions.add(Potion.blindness);
            potions.add(Potion.confusion);
            potions.add(Potion.digSlowdown);
            potions.add(Potion.harm);
            potions.add(Potion.hunger);
            potions.add(Potion.moveSlowdown);
            potions.add(Potion.poison);
            potions.add(Potion.weakness);
        }

        for (Potion potion : potions) {
            if (player.isPotionActive(potion) && ElectricItem.manager.canUse(itemstack, DISCHARGE_CURATIVE)) {
                player.removePotionEffect(potion.id);
                if (!medkitCure) {
                    ElectricItem.manager.discharge(itemstack, DISCHARGE_CURATIVE, 4, true, false, false);
                }
            }
        }

        if (medkitCure) {
            // A generic approach to remove *ANY* negative potion effect that is declared as such
            Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();

            List<Integer> toRemove = new ArrayList<>();
            while (iterator.hasNext()) {
                PotionEffect potioneffect = iterator.next();
                if (Potion.potionTypes[potioneffect.getPotionID()].isBadEffect()) {
                    toRemove.add(potioneffect.getPotionID());
                }
            }

            for (int id : toRemove) {
                if (ElectricItem.manager.canUse(itemstack, DISCHARGE_CURATIVE)) {
                    player.removePotionEffect(id);
                }
            }
        }
    }

    public static boolean readShieldMode(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
        return nbttagcompound.getBoolean("isShieldActive");
    }

    public static void saveShieldMode(ItemStack itemstack, boolean workMode) {
        NBTTagCompound nbttagcompound = GraviSuite.getOrCreateNbtData(itemstack);
        nbttagcompound.setBoolean("isShieldActive", workMode);
    }
}
