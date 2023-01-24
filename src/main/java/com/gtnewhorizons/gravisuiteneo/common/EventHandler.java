package com.gtnewhorizons.gravisuiteneo.common;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.common.DamageSources.EntityDamageSourcePlazma;
import com.gtnewhorizons.gravisuiteneo.util.QuantumShieldHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import gravisuite.GraviSuite;
import gravisuite.ItemGraviChestPlate;
import gravisuite.ServerProxy;
import ic2.api.item.ElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class EventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemCraftedEvent(ItemCraftedEvent event) {
        if (event == null) {
            return;
        }
        if (event.crafting == null) {
            return;
        }

        ItemStack stack = event.crafting.copy();

        UniqueIdentifier uIDCrafted = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        UniqueIdentifier uID_Drill = GameRegistry.findUniqueIdentifierFor(GraviSuite.advDDrill);
        // UniqueIdentifier uID_Plasma = GameRegistry.findUniqueIdentifierFor(GraviSuite.advDDrill);

        if (uIDCrafted != null && uID_Drill != null /* && uID_Plasma != null*/) {
            if (uIDCrafted.equals(uID_Drill)) {
                Achievements.POWERDRILL.triggerAchievement(event.player);
                //  else if (uIDCrafted.equals(uID_Plasma))
                //    GraviAchievement.PLASMAGUN.triggerAchievement(event.player);
            }
        }
    }

    /**
     * For some really nasty "instant-death" functions of other mods
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        ItemStack chest = player.getEquipmentInSlot(3);

        if (chest == null
                || !(chest.getItem() instanceof ItemGraviChestPlate)
                || !GraviSuite.getOrCreateNbtData(chest).getBoolean("isShieldActive")
                || !QuantumShieldHelper.hasValidShieldEquipment(player)) {
            return;
        }

        try {
            // Reset health to max-health
            player.setHealth(player.getMaxHealth());

            // Add a strong regen-effect just in case the
            // previous function does interact in strange ways with tinkers heart-canisters
            player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 200, 4));

            // If that's not cancelable, well, then we can't do anything about it. Just prevent it from crashing
            event.setCanceled(true);

            // *bonk* the shield around the player
            PacketQuantumShield.issue(player.getEntityId(), -1);
        } catch (Exception ignored) {
        }
    }

    /**
     * Give player complete invulnerability to all sorts of damage; As long as he can maintain enough power for the shield
     */
    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        ItemStack chest = player.getEquipmentInSlot(3);
        double energyRequired = event.ammount * 1500.0;

        if (chest == null
                || !(chest.getItem() instanceof ItemGraviChestPlate)
                || !GraviSuite.getOrCreateNbtData(chest).getBoolean("isShieldActive")) {
            return;
        }

        if (!QuantumShieldHelper.hasValidShieldEquipment(player)) {
            // Shield mode is enabled, but player has the wrong armor items
            QuantumShieldHelper.saveShieldMode(chest, false);
            ServerProxy.sendPlayerMessage(
                    player,
                    EnumChatFormatting.RED
                            + StatCollector.translateToLocal("message.graviChestPlate.invalidSetupShieldBreak"));
            QuantumShieldHelper.notifyWorldShieldDown(player);
            return;
        }

        if (!ElectricItem.manager.canUse(chest, energyRequired)) {
            // Not enough energy to absorb damage. Shield "breaks"
            QuantumShieldHelper.saveShieldMode(chest, false);
            ServerProxy.sendPlayerMessage(
                    player,
                    EnumChatFormatting.RED
                            + StatCollector.translateToLocal("message.graviChestPlate.lowpowerShieldBreak"));
            QuantumShieldHelper.notifyWorldShieldDown(player);
        }

        if (event.source instanceof EntityDamageSourcePlazma) {
            Achievements.QSHIELD_PLASMAIMPACT.triggerAchievement(player);
        }
        // Drain half the amount of energy to absorb the damage, as we're already draining ~20kEU/s
        ElectricItem.manager.discharge(
                chest, energyRequired, Properties.ElectricPresets.GraviChestPlate.tier, true, false, false);
        player.hurtResistantTime = 20;
        player.hurtTime = 0;
        event.ammount = 0;
        if (event.isCancelable()) {
            event.setCanceled(true);
        } else {
            // not required..? We set amount to 0 already But just in case someone is using strange ways
            // to damage the player
            player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 20, 4));
        }

        int target = -1;
        if (event.source.getEntity() != null) {
            target = event.source.getEntity().getEntityId();
        }

        if (event.source == DamageSource.fall) {
            target = -2;
        }
        if (event.source == DamageSource.fallingBlock) {
            target = -3;
        }

        player.worldObj.playSoundAtEntity(player, GraviSuiteNeo.MODID + ":qshieldimpact", 1.25f, 1.0f);
        PacketQuantumShield.issue(player.getEntityId(), target);
    }
}
