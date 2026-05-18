package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import gravisuite.ItemAdvancedJetPack;

@Mixin(ItemAdvancedJetPack.class)
public class MixinItemAdvancedJetPack {

    @ModifyConstant(constant = @Constant(doubleValue = 0.03), method = "<init>", remap = false)
    private double gravisuiteneo$getHoverModeFallSpeed(double original) {
        return Properties.AdvTweaks.getHoverModeFallSpeed();
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public double getDamageAbsorptionRatio() {
        return Properties.ArmorPresets.AdvJetPack.absorptionRatio;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    private double getBaseAbsorptionRatio() {
        return 1.0;
    }

    // Redirect GraviSuite's sendPlayerMessage calls in switchFlyState (jetpack engine toggle).
    // switchFlyState has 2 calls: ordinal 0 = engine disabled, ordinal 1 = engine enabled.
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchFlyState",
            remap = false)
    private static void gravisuiteneo$translateJetpackEngineDisabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.advElJetpack.jetpackEngine")
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
    private static void gravisuiteneo$translateJetpackEngineEnabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.advElJetpack.jetpackEngine")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.enabled")));
    }

    // Redirect GraviSuite's sendPlayerMessage calls in switchWorkMode (hover mode toggle).
    // switchWorkMode has 2 calls: ordinal 0 = disabled, ordinal 1 = enabled.
    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchWorkMode",
            remap = false)
    private static void gravisuiteneo$translateHoverModeDisabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.advElJetpack.hoverMode")
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
    private static void gravisuiteneo$translateHoverModeEnabled(EntityPlayer player, String ignored) {
        player.addChatMessage(
                new ChatComponentTranslation("message.advElJetpack.hoverMode")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)).appendText(" ")
                        .appendSibling(new ChatComponentTranslation("message.text.enabled")));
    }

    // NO-OP Helpers.formatMessage() calls in switchFlyState and switchWorkMode.
    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/Helpers;formatMessage(Ljava/lang/String;)Ljava/lang/String;",
                    value = "INVOKE"),
            method = { "switchFlyState", "switchWorkMode" },
            remap = false)
    private static String gravisuiteneo$noopFormatMessage(String key) {
        return "";
    }

    @ModifyExpressionValue(
            method = "useJetpack",
            at = @At(value = "CONSTANT", args = "doubleValue=-0.2"),
            remap = false)
    private static double gravisuiteneo$fixFallingWhenHoldingJumpAndSneak(double original,
            @Local(name = "maxHoverY") double maxHoverY) {
        return maxHoverY + original;
    }
}
