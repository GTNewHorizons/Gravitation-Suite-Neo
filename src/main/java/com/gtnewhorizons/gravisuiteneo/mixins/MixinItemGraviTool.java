package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizons.gravisuiteneo.util.ChatUtil;

import gravisuite.ItemGraviTool;

@Mixin(ItemGraviTool.class)
public class MixinItemGraviTool {

    // Redirect sendPlayerMessage in onItemRightClick (tool mode switch: Hoe/TreeTap/Wrench/Screwdriver).
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateHoeActivated(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("graviTool.snap.Hoe")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)).appendText(" ")
                        .appendSibling(
                                new ChatComponentTranslation("message.text.activated")
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateTreeTapActivated(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("graviTool.snap.TreeTap")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)).appendText(" ").appendSibling(
                                new ChatComponentTranslation("message.text.activated")
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
    }

    @Redirect(
            at = @At(
                    ordinal = 2,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateWrenchActivated(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("graviTool.snap.Wrench")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)).appendText(" ").appendSibling(
                                new ChatComponentTranslation("message.text.activated")
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
    }

    @Redirect(
            at = @At(
                    ordinal = 3,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "onItemRightClick")
    private void gravisuiteneo$translateScrewdriverActivated(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("graviTool.snap.Screwdriver")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE)).appendText(" ")
                        .appendSibling(
                                new ChatComponentTranslation("message.text.activated")
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
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

    // Redirect all sendPlayerMessage calls in energy-check methods (all send message.text.noenergy).
    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = { "onHoeUse", "onWrenchUse", "attemptExtract", "onScrewdriverUse", "canWrench" },
            remap = false)
    private void gravisuiteneo$translateNoEnergy(EntityPlayer player, String ignored) {
        ChatUtil.sendToPlayer(
                player,
                new ChatComponentTranslation("message.text.noenergy")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/Helpers;formatMessage(Ljava/lang/String;)Ljava/lang/String;",
                    value = "INVOKE"),
            method = { "onHoeUse", "onWrenchUse", "attemptExtract", "onScrewdriverUse", "canWrench" },
            remap = false)
    private String gravisuiteneo$noopFormatMessageInEnergyChecks(String key) {
        return "";
    }
}
