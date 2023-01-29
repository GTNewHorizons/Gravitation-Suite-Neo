package com.gtnewhorizons.gravisuiteneo.mixins;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import gravisuite.keyboard.KeyboardClient;

@Mixin(KeyboardClient.class)
public class MixinKeyboardClient {

    @ModifyConstant(method = "<clinit>", constant = { @Constant(intValue = 33), @Constant(intValue = 35) })
    private static int gravineo$modifykeycode(int original) {
        return Keyboard.KEY_NONE;
    }
}
