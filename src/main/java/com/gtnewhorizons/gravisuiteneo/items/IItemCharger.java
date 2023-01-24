package com.gtnewhorizons.gravisuiteneo.items;

import net.minecraft.item.ItemStack;

public interface IItemCharger {

    void doChargeItemStack(ItemStack charger, ItemStack hargee);

    void doChargeItemStackRF(ItemStack charger, ItemStack chargee);
}
