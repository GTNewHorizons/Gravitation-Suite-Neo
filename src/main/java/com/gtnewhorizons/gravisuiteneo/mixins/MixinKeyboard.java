package com.gtnewhorizons.gravisuiteneo.mixins;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.gravisuiteneo.util.QuantumShieldHelper;

import gravisuite.GraviSuite;
import gravisuite.keyboard.Keyboard;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Shadow(remap = false)
    private static Map<EntityPlayer, Boolean> boostKeyState;
    @Shadow(remap = false)
    private static Map<EntityPlayer, Boolean> jumpKeyState;
    @Shadow(remap = false)
    private static Map<EntityPlayer, Boolean> sneakKeyState;
    @Shadow(remap = false)
    private static Map<EntityPlayer, Boolean> forwardKeyState;
    @Shadow(remap = false)
    private static Map<EntityPlayer, Boolean> altKeyState;
    @Shadow(remap = false)
    private static Map<EntityPlayer, Boolean> modeKeyState;

    /**
     * Replace HashMap with WeakHashMap to prevent leaking EntityPlayer (and contained World) Objects.
     */
    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void gravisuiteneo$clinit(CallbackInfo ci) {
        boostKeyState = new WeakHashMap<>();
        jumpKeyState = new WeakHashMap<>();
        sneakKeyState = new WeakHashMap<>();
        forwardKeyState = new WeakHashMap<>();
        altKeyState = new WeakHashMap<>();
        modeKeyState = new WeakHashMap<>();
    }

    @Inject(
            at = @At(
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1,
                    remap = true,
                    target = "Lnet/minecraft/entity/player/EntityPlayer;inventory:Lnet/minecraft/entity/player/InventoryPlayer;",
                    value = "FIELD"),
            method = "processKeyPressed",
            remap = false)
    private void gravisuiteneo$switchGraviChestShieldMode(EntityPlayer player, int keyPressed, CallbackInfo ci) {
        ItemStack itemstack = player.inventory.armorInventory[2];
        if (itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate && Keyboard.isSneakKeyDown(player)) {
            QuantumShieldHelper.switchShieldMode(player, itemstack);
        }
    }

    @Inject(at = @At("TAIL"), method = "processKeyPressed", remap = false)
    private void gravisuiteneo$performGraviChestMedkitAction(EntityPlayer player, int keyPressed, CallbackInfo ci) {
        if (keyPressed == 4) {
            ItemStack itemstack = player.inventory.armorInventory[2];
            if (itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
                QuantumShieldHelper.performMedkitAction(player, itemstack);
            }
        }
    }
}
