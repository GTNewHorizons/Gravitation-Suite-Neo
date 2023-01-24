package com.gtnewhorizons.gravisuiteneo.mixins;

import gravisuite.Helpers;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

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
        return EnumChatFormatting.getTextWithoutFormattingCodes(StatCollector.translateToLocal(inputString));
    }
}
