package com.gtnewhorizons.gravisuiteneo.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import gravisuite.GraviSuite;
import gravisuite.client.ClientTickHandler;

@Mixin(ClientTickHandler.class)
public class MixinClientTickHandler {

    /**
     * @author glowredman
     * @reason Getting the IDs of the IC2 hotkeys is useless now
     */
    @Overwrite(remap = false)
    public static void onTickClient() {
        if (ClientTickHandler.mc.theWorld != null) {
            GraviSuite.keyboard.sendKeyUpdate(ClientTickHandler.mc.thePlayer);
        }
    }

}
