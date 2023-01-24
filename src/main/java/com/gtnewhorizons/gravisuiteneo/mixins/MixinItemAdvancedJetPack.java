package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.common.Properties;
import gravisuite.ItemAdvancedJetPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemAdvancedJetPack.class)
public class MixinItemAdvancedJetPack {

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