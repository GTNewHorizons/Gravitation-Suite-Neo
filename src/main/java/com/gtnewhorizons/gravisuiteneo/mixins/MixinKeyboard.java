package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.gravisuiteneo.util.QuantumShieldHelper;

import gravisuite.GraviSuite;
import gravisuite.keyboard.Keyboard;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(
            at = @At(
                    args = "array=get",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1,
                    remap = true,
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;armorInventory:[Lnet/minecraft/item/ItemStack;",
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
