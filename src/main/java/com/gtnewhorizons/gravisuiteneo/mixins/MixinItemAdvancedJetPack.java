package com.gtnewhorizons.gravisuiteneo.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

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

    @ModifyExpressionValue(
            method = "useJetpack",
            at = @At(value = "CONSTANT", args = "doubleValue=-0.2"),
            remap = false)
    private static double gravisuiteneo$fixFallingWhenHoldingJumpAndSneak(double original,
            @Local(name = "maxHoverY") double maxHoverY) {
        return maxHoverY + original;
    }
}
