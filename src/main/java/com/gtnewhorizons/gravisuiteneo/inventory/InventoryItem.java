package com.gtnewhorizons.gravisuiteneo.inventory;

import com.gtnewhorizons.gravisuiteneo.items.ItemPlasmaCell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class InventoryItem implements IInventory {

    /** Provides NBT Tag Compound to reference */
    private final ItemStack invItem;

    /** Defining your inventory size this way is handy */
    public static final int INV_SIZE = 1;

    /** Inventory's size must be same as number of slots you add to the Container class */
    private final ItemStack[] inventory = new ItemStack[INV_SIZE];

    /**
     * @param stack - the ItemStack to which this inventory belongs
     */
    public InventoryItem(ItemStack stack) {
        this.invItem = stack;

        // Create a new NBT Tag Compound if one doesn't already exist, or you will crash
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        // note that it's okay to use stack instead of invItem right there
        // both reference the same memory location, so whatever you change using
        // either reference will change in the other

        // Read the inventory contents from NBT
        this.readFromNBT(stack.getTagCompound());
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = this.getStackInSlot(slot);
        if (stack != null) {
            if (stack.stackSize > amount) {
                stack = stack.splitStack(amount);
                // Don't forget this line or your inventory will not be saved!
                this.markDirty();
            } else {
                // this method also calls onInventoryChanged, so we don't need to call it again
                this.setInventorySlotContents(slot, null);
            }
        }
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = this.getStackInSlot(slot);
        this.setInventorySlotContents(slot, null);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int pSlot, ItemStack pItemStack) {
        this.inventory[pSlot] = pItemStack;

        if (pItemStack != null && pItemStack.stackSize > this.getInventoryStackLimit()) {
            pItemStack.stackSize = this.getInventoryStackLimit();
        }

        // Don't forget this line or your inventory will not be saved!
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "Inventory Item";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) != null && this.getStackInSlot(i).stackSize == 0) {
                this.inventory[i] = null;
            }
        }
        this.writeToNBT(this.invItem.getTagCompound());
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    /**
     * This method doesn't seem to do what it claims to do, as
     * items can still be left-clicked and placed in the inventory
     * even when this returns false
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemPlasmaCell;
    }

    /**
     * A custom method to read our inventory from an ItemStack's NBT compound
     */
    public void readFromNBT(NBTTagCompound compound) {
        // Gets the custom taglist we wrote to this compound, if any
        NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < items.tagCount(); ++i) {
            // 1.7.2+ change to items.getCompoundTagAt(i)
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getInteger("Slot");

            // Just double-checking that the saved slot index is within our inventory array bounds
            if (slot >= 0 && slot < this.getSizeInventory()) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(item);
                // FMLLog.info("Item loaded %s", inventory[slot].getUnlocalizedName());
            }
        }
    }

    /**
     * A custom method to write our inventory to an ItemStack's NBT compound
     */
    public void writeToNBT(NBTTagCompound tagcompound) {
        // Create a new NBT Tag List to store itemstacks as NBT Tags
        NBTTagList items = new NBTTagList();

        for (int i = 0; i < this.getSizeInventory(); ++i) {
            // Only write stacks that contain items
            if (this.getStackInSlot(i) != null) {
                // Make a new NBT Tag Compound to write the itemstack and slot index to
                NBTTagCompound item = new NBTTagCompound();
                item.setInteger("Slot", i);

                // Writes the itemstack in slot(i) to the Tag Compound we just made
                this.getStackInSlot(i).writeToNBT(item);

                // add the tag compound to our tag list
                items.appendTag(item);
            }
        }
        // Add the TagList to the ItemStack's Tag Compound with the name "ItemInventory"
        tagcompound.setTag("ItemInventory", items);
        // FMLLog.info("SavedTag: %s", items.toString());
    }
}
