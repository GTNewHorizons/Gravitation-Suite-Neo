package com.gtnewhorizons.gravisuiteneo.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPlasmaLauncher extends Container {

    private final InventoryItem inventory;
    private static final int INV_START = InventoryItem.INV_SIZE,
            INV_END = INV_START + 26,
            HOTBAR_START = INV_END + 1,
            HOTBAR_END = HOTBAR_START + 8;

    public ContainerPlasmaLauncher(
        InventoryPlayer inventoryPlayer, InventoryItem inventoryItem) {
        this.inventory = inventoryItem;

        int i;
        for (i = 0; i < InventoryItem.INV_SIZE; ++i) {
            this.addSlotToContainer(new SlotPlasmaAmmo(this.inventory, i, 142 + 18 * (i / 4), 42 + 18 * (i % 4)));
        }
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    /**
     * You should override this method to prevent the player from moving the stack that
     * opened the inventory, otherwise if the player moves it, the inventory will not
     * be able to save properly
     */
    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
        // this will prevent the player from interacting with the item that opened the inventory:
        if (slot >= 0 && this.getSlot(slot) != null && this.getSlot(slot).getStack() == player.getHeldItem()) {
            return null;
        }
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        // be sure to return the inventory's isUseableByPlayer method
        // if you defined special behavior there:
        return inventory.isUseableByPlayer(entityplayer);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < INV_START) {
                if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END + 1, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else {
                if (!this.mergeItemStack(itemstack1, 0, INV_START, false)) {
                    return null;
                }
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean backwards) {
        boolean flag1 = false;
        int k = backwards ? end - 1 : start;
        Slot slot;
        ItemStack itemstack1;

        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!backwards && k < end || backwards && k >= start)) {
                slot = (Slot) this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (!slot.isItemValid(stack)) {
                    k += backwards ? -1 : 1;
                    continue;
                }

                if (itemstack1 != null
                        && itemstack1.getItem() == stack.getItem()
                        && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage())
                        && ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
                    int l = itemstack1.stackSize + stack.stackSize;

                    if (l <= stack.getMaxStackSize() && l <= slot.getSlotStackLimit()) {
                        stack.stackSize = 0;
                        itemstack1.stackSize = l;
                        this.inventory.markDirty();
                        flag1 = true;
                    } else if (itemstack1.stackSize < stack.getMaxStackSize() && l < slot.getSlotStackLimit()) {
                        stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = stack.getMaxStackSize();
                        this.inventory.markDirty();
                        flag1 = true;
                    }
                }

                k += backwards ? -1 : 1;
            }
        }
        if (stack.stackSize > 0) {
            k = backwards ? end - 1 : start;
            while (!backwards && k < end || backwards && k >= start) {
                slot = (Slot) this.inventorySlots.get(k);
                itemstack1 = slot.getStack();

                if (!slot.isItemValid(stack)) {
                    k += backwards ? -1 : 1;
                    continue;
                }

                if (itemstack1 == null) {
                    int l = stack.stackSize;
                    if (l <= slot.getSlotStackLimit()) {
                        slot.putStack(stack.copy());
                        stack.stackSize = 0;
                        this.inventory.markDirty();
                        flag1 = true;
                        break;
                    } else {
                        this.putStackInSlot(
                                k, new ItemStack(stack.getItem(), slot.getSlotStackLimit(), stack.getItemDamage()));
                        stack.stackSize -= slot.getSlotStackLimit();
                        this.inventory.markDirty();
                        flag1 = true;
                    }
                }

                k += backwards ? -1 : 1;
            }
        }

        return flag1;
    }
}
