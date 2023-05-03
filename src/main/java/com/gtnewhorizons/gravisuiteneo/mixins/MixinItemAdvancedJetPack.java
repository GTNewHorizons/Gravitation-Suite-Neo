package com.gtnewhorizons.gravisuiteneo.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.gtnewhorizons.gravisuiteneo.common.Properties;

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
}
