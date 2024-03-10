package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import gravisuite.keyboard.KeyboardClient;
import ic2.api.util.Keys;

@Mixin(KeyboardClient.class)
public class MixinKeyboardClient {

    @Shadow(remap = false)
    private static boolean lastKeyModeState;

    @ModifyConstant(method = "<clinit>", constant = { @Constant(intValue = 33), @Constant(intValue = 35) })
    private static int gravineo$modifykeycode(int original) {
        return Keyboard.KEY_NONE;
    }

    /**
     * @author glowredman
     * @reason Use IC2 API instead of hacky workaround
     */
    @Overwrite(remap = false)
    public static boolean isBoostKeyDown(EntityPlayer player) {
        return Keys.instance.isBoostKeyDown(player);
    }

    /**
     * @author glowredman
     * @reason Use IC2 API instead of hacky workaround
     */
    @Overwrite(remap = false)
    public static boolean isAltKeyDown(EntityPlayer player) {
        return Keys.instance.isAltKeyDown(player);
    }

    /**
     * @author glowredman
     * @reason Use IC2 API instead of hacky workaround
     */
    @Overwrite(remap = false)
    public static boolean isModeKeyPress(EntityPlayer player) {
        if (Keys.instance.isModeSwitchKeyDown(player)) {
            if (!lastKeyModeState) {
                lastKeyModeState = true;
                sendModeKey(player);
            }
            return true;
        }
        lastKeyModeState = false;
        return false;
    }

    /**
     * @author glowredman
     * @reason Use IC2 API instead of hacky workaround
     */
    @Overwrite(remap = false)
    public static boolean isJumpKeyDown(EntityPlayer player) {
        return Keys.instance.isJumpKeyDown(player);
    }

    /**
     * @author glowredman
     * @reason Use IC2 API instead of hacky workaround
     */
    @Overwrite(remap = false)
    public static boolean isSneakKeyDown(EntityPlayer player) {
        return Keys.instance.isSneakKeyDown(player);
    }

    @Shadow(remap = false)
    public static void sendModeKey(EntityPlayer player) {
        throw new IllegalStateException("Failed to shade KeyboardClient.sendModeKey(EntityPlayer)");
    }
}
