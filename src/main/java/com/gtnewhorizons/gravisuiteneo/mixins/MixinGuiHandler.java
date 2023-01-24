package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.client.GuiPlasmaLauncher;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.inventory.ContainerPlasmaLauncher;
import com.gtnewhorizons.gravisuiteneo.inventory.InventoryItem;
import gravisuite.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiHandler.class)
public class MixinGuiHandler {

    @Inject(at = @At("RETURN"), cancellable = true, method = "getServerGuiElement", remap = false)
    private void gravisuiteneo$getServerGuiElement(
            int ID, EntityPlayer player, World world, int X, int Y, int Z, CallbackInfoReturnable<Object> cir) {
        if (ID == Properties.GUIID_PLASMALAUNCHER) {
            cir.setReturnValue(new ContainerPlasmaLauncher(player.inventory, new InventoryItem(player.getHeldItem())));
        }
    }

    @Inject(at = @At("TAIL"), cancellable = true, method = "getClientGuiElement", remap = false)
    private void gravisuiteneo$getClientGuiElement(
            int ID, EntityPlayer player, World world, int X, int Y, int Z, CallbackInfoReturnable<Object> cir) {
        //        if(ID == Properties.GUIID_ADVDRILL) {
        //            cir.setReturnValue(new GuiAdvDrill(player.inventory));
        //            return;
        //        }
        if (ID == Properties.GUIID_PLASMALAUNCHER) {
            cir.setReturnValue(new GuiPlasmaLauncher(new ContainerPlasmaLauncher(player.inventory, new InventoryItem(player.getHeldItem()))));
        }
    }
}
