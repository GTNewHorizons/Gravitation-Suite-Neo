package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gravisuite.Helpers;

@Mixin(Helpers.class)
public class MixinHelpers {

    /**
     * @deprecated Use {@link StatCollector#translateToLocal(String)} instead
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Deprecated
    @Overwrite(remap = false)
    public static String formatMessage(String inputString) {
        return StatCollector.translateToLocal(inputString);
    }
}
