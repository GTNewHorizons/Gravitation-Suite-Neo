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

    // Redirect GraviSuite's sendPlayerMessage calls in switchFlyState (hover/engine toggle).
    // §e prefix = hover mode (YELLOW), no §e = jetpack engine; §a = enabled, §c = disabled.
    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "switchFlyState",
            remap = false)
    private static void gravisuiteneo$translateJetpackMessage(EntityPlayer player, String message) {
        boolean isHover = message.contains("§e");
        boolean enabled = message.contains("§a");
        String modeKey = isHover ? "message.advElJetpack.hoverMode" : "message.advElJetpack.jetpackEngine";
        EnumChatFormatting color = isHover ? EnumChatFormatting.YELLOW
                : (enabled ? EnumChatFormatting.GREEN : EnumChatFormatting.RED);
        player.addChatMessage(
                new ChatComponentTranslation(modeKey).setChatStyle(new ChatStyle().setColor(color)).appendText(" ")
                        .appendSibling(
                                new ChatComponentTranslation(
                                        enabled ? "message.text.enabled" : "message.text.disabled")));
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
