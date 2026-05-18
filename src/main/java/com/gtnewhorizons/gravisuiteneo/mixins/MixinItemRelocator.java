package com.gtnewhorizons.gravisuiteneo.mixins;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.gtnewhorizons.gravisuiteneo.common.EntityPlasmaBallMKII;
import com.llamalad7.mixinextras.sugar.Local;

import gravisuite.EntityPlasmaBall;
import gravisuite.ItemRelocator;
import gravisuite.ItemRelocator.TeleportPoint;

@Mixin(ItemRelocator.class)
public class MixinItemRelocator {

    @Shadow(remap = false)
    private double maxCharge;

    @Shadow(remap = false)
    private int energyPerDimesionTp;

    @Inject(at = @At("TAIL"), method = "<init>", remap = false)
    private void gravisuiteneo$assertCorrectEnergyPerDimesionTp(ToolMaterial toolMaterial, CallbackInfo ci) {
        if (this.energyPerDimesionTp > this.maxCharge) {
            this.energyPerDimesionTp = (int) (this.maxCharge / 2.0 - 100.0);
        }
    }

    @Redirect(
            at = @At(remap = false, target = "gravisuite/EntityPlasmaBall", value = "NEW"),
            method = "onItemRightClick")
    private EntityPlasmaBall gravisuiteneo$constructEntityPlasmaBall(World world, EntityLivingBase entityLiving,
            TeleportPoint tpPoint, byte entityType) {
        return new EntityPlasmaBallMKII(world, entityLiving, tpPoint, entityType);
    }

    @Redirect(
            at = @At(remap = false, target = "Ljava/util/List;size()I", value = "INVOKE"),
            method = "addNewTeleportPoint",
            remap = false)
    private static int gravisuiteneo$removeMemoryLimit(List<TeleportPoint> tpList) {
        return 0;
    }

    @Inject(
            at = @At(
                    remap = false,
                    shift = Shift.BEFORE,
                    target = "Lgravisuite/Helpers;teleportEntity(Lnet/minecraft/entity/Entity;Lgravisuite/ItemRelocator$TeleportPoint;)Lnet/minecraft/entity/Entity;",
                    value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "teleportPlayer",
            remap = false)
    private void gravisuiteneo$sendTeleportingNowMessage(EntityPlayer player, ItemStack itemStack, String tpName,
            CallbackInfo ci, TeleportPoint point) {
        player.addChatMessage(
                new ChatComponentTranslation("message.relocator.text.teleportingnow")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)).appendText(" ").appendSibling(
                                new ChatComponentText(point.pointName)
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
    }

    // Redirect sendPlayerMessage in onItemRightClick (mode switching).
    // ordinals 0-2: mode change (personal/translocator/portal), 3-4: mode disabled, 5: no energy, 6: no default point.
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateModePersonal(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.text.mode").appendText(": ")
                        .appendSibling(new ChatComponentTranslation("message.relocator.mode.personal")));
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateModeTranslocator(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.text.mode").appendText(": ")
                        .appendSibling(new ChatComponentTranslation("message.relocator.mode.translocator")));
    }

    @Redirect(
            at = @At(
                    ordinal = 2,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateModePortal(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.text.mode").appendText(": ")
                        .appendSibling(new ChatComponentTranslation("message.relocator.mode.portal")));
    }

    @Redirect(
            at = @At(
                    ordinal = 3,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateTranslocatorDisabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.relocator.text.modeTranslocatorDisabled")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Redirect(
            at = @At(
                    ordinal = 4,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translatePortalDisabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.relocator.text.modePortalDisabled")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Redirect(
            at = @At(
                    ordinal = 5,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateRelocatorNoEnergy(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.text.noenergy")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Redirect(
            at = @At(
                    ordinal = 6,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateNoDefaultPoint(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.relocator.text.noDefaultPoint")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
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

    // Redirect sendPlayerMessage in addNewTeleportPoint.
    // ordinal 0 (memoryFull) is never reached due to gravisuiteneo$removeMemoryLimit; skip it.
    // ordinal 1 = point already exists, ordinal 2 = point added.
    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "addNewTeleportPoint",
            remap = false)
    private static void gravisuiteneo$translatePointExists(EntityPlayer player, String ignored,
            @Local(argsOnly = true) TeleportPoint tp) {
        player.addChatMessage(
                new ChatComponentText(tp.pointName).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))
                        .appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.relocator.text.pointExists")));
    }

    @Redirect(
            at = @At(
                    ordinal = 2,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "addNewTeleportPoint",
            remap = false)
    private static void gravisuiteneo$translatePointAdded(EntityPlayer player, String ignored,
            @Local(argsOnly = true) TeleportPoint tp) {
        player.addChatMessage(
                new ChatComponentText(tp.pointName).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                        .appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.relocator.text.poindAdded")));
    }

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/Helpers;formatMessage(Ljava/lang/String;)Ljava/lang/String;",
                    value = "INVOKE"),
            method = "addNewTeleportPoint",
            remap = false)
    private static String gravisuiteneo$noopFormatMessageInAddNewTeleportPoint(String key) {
        return "";
    }

    // Redirect sendPlayerMessage in setDefaultPoint.
    // ordinal 0 = default point set (success), ordinal 1 = point not found (error).
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "setDefaultPoint",
            remap = false)
    private static void gravisuiteneo$translateDefaultPointSet(EntityPlayer player, String ignored,
            @Local(argsOnly = true) String pointName) {
        player.addChatMessage(
                new ChatComponentTranslation("message.relocator.text.defaultPointSet")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).appendText(" ").appendSibling(
                                new ChatComponentText(pointName)
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE))));
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "setDefaultPoint",
            remap = false)
    private static void gravisuiteneo$translatePointNotFound(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.relocator.text.noPoint")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/Helpers;formatMessage(Ljava/lang/String;)Ljava/lang/String;",
                    value = "INVOKE"),
            method = "setDefaultPoint",
            remap = false)
    private static String gravisuiteneo$noopFormatMessageInSetDefaultPoint(String key) {
        return "";
    }

    // Redirect sendPlayerMessage in teleportPlayer (no energy).
    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "teleportPlayer",
            remap = false)
    private void gravisuiteneo$translateTeleportNoEnergy(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.text.noenergy")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/Helpers;formatMessage(Ljava/lang/String;)Ljava/lang/String;",
                    value = "INVOKE"),
            method = "teleportPlayer",
            remap = false)
    private String gravisuiteneo$noopFormatMessageInTeleportPlayer(String key) {
        return "";
    }
}
