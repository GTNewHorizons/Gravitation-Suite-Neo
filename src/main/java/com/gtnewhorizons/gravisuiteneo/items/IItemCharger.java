package com.gtnewhorizons.gravisuiteneo.items;

import net.minecraft.item.ItemStack;

public interface IItemCharger {

    void doChargeItemStack(ItemStack charger, ItemStack chargee);

    void doChargeItemStackRF(ItemStack charger, ItemStack chargee);
}
