package com.gtnewhorizons.gravisuiteneo.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gtnewhorizons.gravisuiteneo.common.Properties;

import gravisuite.ItemAdvancedNanoChestPlate;

@Mixin(ItemAdvancedNanoChestPlate.class)
public class MixinItemAdvancedNanoChestPlate {

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public double getDamageAbsorptionRatio() {
        return Properties.ArmorPresets.AdvNanoChestPlate.absorptionRatio;
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
