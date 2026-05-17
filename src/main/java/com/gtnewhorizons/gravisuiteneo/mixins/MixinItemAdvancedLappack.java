package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.gtnewhorizons.gravisuiteneo.common.Achievements;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.items.IItemCharger;
import com.gtnewhorizons.gravisuiteneo.items.ItemEpicLappack;

import cofh.api.energy.IEnergyContainerItem;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedLappack;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

@Mixin(ItemAdvancedLappack.class)
public class MixinItemAdvancedLappack implements IItemCharger {

    @Shadow(remap = false)
    private int transferLimit;

    @Shadow(remap = false)
    private int tier;

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public double getDamageAbsorptionRatio() {
        return Properties.ArmorPresets.AdvLapPack.absorptionRatio;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    private double getBaseAbsorptionRatio() {
        return 1.0;
    }

    // Redirect GraviSuite's sendPlayerMessage calls in onItemRightClick (power supply toggle).
    // onItemRightClick has 2 calls: ordinal 0 = disabled, ordinal 1 = enabled.
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translatePowerSupplyDisabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.text.powerSupply")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.disabled")));
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translatePowerSupplyEnabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.text.powerSupply")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.enabled")));
    }

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/Helpers;formatMessage(Ljava/lang/String;)Ljava/lang/String;",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private String gravisuiteneo$noopFormatMessageInOnItemRightClick(String key) {
        return "";
    }

    @Inject(
            at = @At(ordinal = 1, remap = false, target = "java/lang/StringBuilder", value = "NEW"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "onItemRightClick")
    private void gravisuiteneo$triggerAchievement(ItemStack itemStack, World world, EntityPlayer player,
            CallbackInfoReturnable<ItemStack> cir, Integer toolMode) {
        if (itemStack.getItem() instanceof ItemEpicLappack) {
            player.triggerAchievement(Achievements.EPIC_LAPPACK);
        }
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public boolean onTick(EntityPlayer player, ItemStack itemstack) {
        int toolMode = ItemAdvancedLappack.readToolMode(itemstack);
        if (toolMode == 1 && GraviSuite.isSimulating()) {
            ItemStack armorItemStack = player.inventory.armorInventory[2];
            if (armorItemStack == null) {
                return true;
            }
            if (armorItemStack.getItem() instanceof IElectricItem) {
                this.doChargeItemStack(itemstack, armorItemStack);
            }
            if (armorItemStack.getItem() instanceof IEnergyContainerItem) {
                this.doChargeItemStackRF(itemstack, armorItemStack);
            }
        }
        return true;
    }

    @Override
    public void doChargeItemStack(ItemStack charger, ItemStack chargee) {
        int energyPacket = this.transferLimit;
        int mainCharge = ItemAdvancedLappack.getCharge(charger);
        if (mainCharge <= this.transferLimit) {
            energyPacket = mainCharge;
        }

        double sentPacket = ElectricItem.manager.charge(chargee, energyPacket, this.tier, false, false);
        if (sentPacket > 0.0D) {
            ElectricItem.manager.discharge(charger, sentPacket, this.tier, false, false, false);
        }
    }

    @Override
    public void doChargeItemStackRF(ItemStack charger, ItemStack chargee) {
        int energyPacket = this.transferLimit;
        int mainCharge = ItemAdvancedLappack.getCharge(charger);
        if (mainCharge <= this.transferLimit) {
            energyPacket = mainCharge;
        }

        double sentPacket = ((IEnergyContainerItem) chargee.getItem()).receiveEnergy(chargee, energyPacket * 4, false)
                / 4.0D;
        if (sentPacket > 0.0D) {
            ElectricItem.manager.discharge(charger, sentPacket, this.tier, false, false, false);
        }
    }
}
