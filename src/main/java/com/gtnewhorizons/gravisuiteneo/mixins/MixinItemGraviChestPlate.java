package com.gtnewhorizons.gravisuiteneo.mixins;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.util.ChatUtil;
import com.gtnewhorizons.gravisuiteneo.util.QuantumShieldHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.ItemGraviChestPlate;
import gregtech.api.hazards.Hazard;
import gregtech.api.hazards.IHazardProtector;
import ic2.api.item.ElectricItem;

@Mixin(ItemGraviChestPlate.class)
@Optional.Interface(iface = "gregtech.api.hazards.IHazardProtector", modid = "gregtech_nh")
public class MixinItemGraviChestPlate implements IHazardProtector {

    @Inject(
            at = @At(opcode = Opcodes.IFEQ, ordinal = 7, value = "JUMP"),
            cancellable = true,
            method = "onArmorTick",
            remap = false)
    private void gravisuiteneo$handleShieldAndNanobots(World worldObj, EntityPlayer player, ItemStack itemStack,
            CallbackInfo ci) {
        if (!QuantumShieldHelper.readShieldMode(itemStack)) return;

        if (!QuantumShieldHelper.hasValidShieldEquipment(player)) {
            ChatUtil.sendToPlayer(
                    player,
                    new ChatComponentTranslation("message.graviChestPlate.invalidSetupShieldBreak")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            QuantumShieldHelper.saveShieldMode(itemStack, false);
            QuantumShieldHelper.notifyWorldShieldDown(player);
            ci.cancel();
            return;
        }

        if (!player.capabilities.isCreativeMode) {
            if (ItemGraviChestPlate.getCharge(itemStack) < QuantumShieldHelper.DISCHARGE_IDLE) {
                ChatUtil.sendToPlayer(
                        player,
                        new ChatComponentTranslation("message.graviChestPlate.lowpowerShieldBreak")
                                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                QuantumShieldHelper.saveShieldMode(itemStack, false);
                QuantumShieldHelper.notifyWorldShieldDown(player);
                ci.cancel();
                return;
            }
            ElectricItem.manager.discharge(itemStack, QuantumShieldHelper.DISCHARGE_IDLE, 4, false, false, false);
            QuantumShieldHelper.runHealthMonitor(player, itemStack);
        }
    }

    @ModifyExpressionValue(
            at = @At(target = "Lnet/minecraft/entity/player/EntityPlayer;isBurning()Z", value = "INVOKE", remap = true),
            method = "onArmorTick",
            remap = false)
    private boolean gravisuiteneo$checkCanExtinguish(boolean original, World worldObj, EntityPlayer player,
            ItemStack itemStack) {
        if (original && ElectricItem.manager.canUse(itemStack, QuantumShieldHelper.DISCHARGE_EXTINGUISH)
                && !player.isPotionActive(Potion.fireResistance)) {
            ElectricItem.manager.discharge(itemStack, QuantumShieldHelper.DISCHARGE_EXTINGUISH, 4, true, false, false);
            return true;
        }
        return false;
    }

    @Inject(at = @At("TAIL"), method = "onArmorTick", remap = false)
    private void gravisuiteneo$curePotions(World worldObj, EntityPlayer player, ItemStack itemStack, CallbackInfo ci) {
        QuantumShieldHelper.curePotions(itemStack, player, false);
    }

    @SideOnly(Side.CLIENT)
    @Inject(at = @At("TAIL"), method = "addInformation")
    private void gravisuiteneo$addShieldInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltip,
            boolean advancedTooltips, CallbackInfo ci) {
        String shieldStatus;
        if (QuantumShieldHelper.readShieldMode(itemStack)) {
            shieldStatus = EnumChatFormatting.GREEN + StatCollector.translateToLocal("message.text.on");
        } else {
            shieldStatus = EnumChatFormatting.RED + StatCollector.translateToLocal("message.text.off");
        }
        tooltip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("message.graviChestPlate.shieldMode")
                        + ": "
                        + shieldStatus);
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public int getEnergyPerDamage() {
        return 3000;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public double getDamageAbsorptionRatio() {
        return Properties.ArmorPresets.GraviChestPlate.absorptionRatio;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    private double getBaseAbsorptionRatio() {
        return 1.0;
    }

    // Redirect GraviSuite's sendPlayerMessage calls in switchFlyState (gravity engine toggle).
    // Bytecode order (javap) of the 3 calls: ordinal 0 = disabled, ordinal 1 = enabled, ordinal 2 = low energy.
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchFlyState",
            remap = false)
    private static void gravisuiteneo$translateGravityEngineDisabled(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.graviChestPlate.gravitationEngine")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.disabled")));
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchFlyState",
            remap = false)
    private static void gravisuiteneo$translateGravityEngineEnabled(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.graviChestPlate.gravitationEngine")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.enabled")));
    }

    @Redirect(
            at = @At(
                    ordinal = 2,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchFlyState",
            remap = false)
    private static void gravisuiteneo$translateGravityEngineLowEnergy(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.graviChestPlate.lowEnergy")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    // Redirect GraviSuite's sendPlayerMessage calls in switchWorkMode (levitation toggle).
    // switchWorkMode has 2 calls: ordinal 0 = disabled, ordinal 1 = enabled.
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchWorkMode",
            remap = false)
    private static void gravisuiteneo$translateLevitationModeDisabled(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.graviChestPlate.levitationMode")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.disabled")));
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchWorkMode",
            remap = false)
    private static void gravisuiteneo$translateLevitationModeEnabled(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.graviChestPlate.levitationMode")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.enabled")));
    }

    // NO-OP Helpers.formatMessage() calls — strings are discarded because every sendPlayerMessage
    // call site in these methods is redirected above.
    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/Helpers;formatMessage(Ljava/lang/String;)Ljava/lang/String;",
                    value = "INVOKE"),
            method = { "switchFlyState", "onArmorTick", "switchWorkMode" },
            remap = false)
    private static String gravisuiteneo$noopFormatMessage(String key) {
        return "";
    }

    // Redirect GraviSuite's sendPlayerMessage calls in onArmorTick.
    // onArmorTick has 2 calls: ordinal 0 = shutdown, ordinal 1 = noEnergyToBoost.
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onArmorTick",
            remap = false)
    private void gravisuiteneo$translateShutdownMessage(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.graviChestPlate.shutdown")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onArmorTick",
            remap = false)
    private void gravisuiteneo$translateNoEnergyToBoostMessage(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.graviChestPlate.noEnergyToBoost")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Override
    @Optional.Method(modid = "gregtech_nh")
    public boolean protectsAgainst(ItemStack itemStack, Hazard hazard) {
        return true;
    }
}
