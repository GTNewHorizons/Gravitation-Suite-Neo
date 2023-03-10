package com.gtnewhorizons.gravisuiteneo.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.gravisuiteneo.items.ItemPlasmaCell;

public class SlotPlasmaAmmo extends Slot {

    public SlotPlasmaAmmo(IInventory inv, int index, int xPos, int yPos) {
        super(inv, index, xPos, yPos);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemPlasmaCell;
    }
}
