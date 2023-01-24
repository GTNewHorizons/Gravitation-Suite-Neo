package com.gtnewhorizons.gravisuiteneo.mixins;

import cofh.api.energy.IEnergyContainerItem;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedLappack;
import gravisuite.ItemUltimateLappack;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemUltimateLappack.class)
public class MixinItemUltimateLappack extends MixinItemAdvancedLappack {

    @Override
    public boolean onTick(EntityPlayer player, ItemStack itemstack) {
        try {
            int toolMode = ItemAdvancedLappack.readToolMode(itemstack);
            if (GraviSuite.isSimulating() && toolMode == 1) {
                for (ItemStack is : player.inventory.armorInventory) {
                    if (is == null) {
                        continue;
                    }
                    if (is.getItem() instanceof IElectricItem) {
                        this.doChargeItemStack(itemstack, is);
                    }
                    if (is.getItem() instanceof IEnergyContainerItem) {
                        this.doChargeItemStackRF(itemstack, is);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
