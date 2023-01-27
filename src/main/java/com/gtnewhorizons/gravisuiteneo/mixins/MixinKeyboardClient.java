package com.gtnewhorizons.gravisuiteneo.mixins;

import gravisuite.keyboard.KeyboardClient;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyboardClient.class)
public class MixinKeyboardClient {
    @Shadow(remap = false)
    public static KeyBinding flyKey = new KeyBinding("Gravi Fly Key", Keyboard.KEY_NONE, "GraviSuite");

    @Shadow(remap = false)
    public static KeyBinding displayHUDKey = new KeyBinding("Gravi Display Hud", Keyboard.KEY_NONE, "GraviSuite");
}
