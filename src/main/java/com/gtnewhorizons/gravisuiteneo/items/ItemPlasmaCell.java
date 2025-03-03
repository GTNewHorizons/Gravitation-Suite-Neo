package com.gtnewhorizons.gravisuiteneo.items;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import com.gtnewhorizons.gravisuiteneo.client.ICustomItemBars;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.util.FluidHelper;

import gravisuite.IItemTickListener;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

public class ItemPlasmaCell extends Item
        implements IElectricItem, IItemTickListener, ICustomItemBars, IFluidContainerItem {

    private static final String NBT_FLUID_TAG = "Fluid";
    private static final int CAPACITY = 64000;
    private final int maxCharge;
    private final int transferLimit;
    private final int tier;

    public ItemPlasmaCell() {
        this.maxCharge = Properties.ElectricPresets.PlasmaCell.maxCharge;
        this.transferLimit = Properties.ElectricPresets.PlasmaCell.transferLimit;
        this.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        this.tier = Properties.ElectricPresets.PlasmaCell.tier;
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setUnlocalizedName("PlasmaCell");
        this.setTextureName(GraviSuiteNeo.MODID + ":itemPlasmaCell");
    }

    /*
     * IFluidContainerItem start
     */

    @Override
    public FluidStack getFluid(ItemStack container) {
        NBTTagCompound tTagCompound = getOrCreateNbtData(container);
        NBTTagCompound tFluidTag = tTagCompound.getCompoundTag(NBT_FLUID_TAG);

        return FluidStack.loadFluidStackFromNBT(tFluidTag);
    }

    public static NBTTagCompound getOrCreateNbtData(ItemStack itemStack) {
        NBTTagCompound ret = itemStack.getTagCompound();
        if (ret == null) {
            ret = new NBTTagCompound();
            itemStack.setTagCompound(ret);
        }
        return ret;
    }

    @Override
    public int getCapacity(ItemStack container) {
        return CAPACITY;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        // Block everything that has no PlasmaFuelValue
        if (GraviSuiteNeoRegistry.getPlasmaEfficiency(resource) < 1) {
            return 0;
        }

        if (container.stackSize != 1) {
            return 0;
        }

        if (resource == null) {
            return 0;
        }

        NBTTagCompound tagCompound = getOrCreateNbtData(container);
        NBTTagCompound fluidTag = tagCompound.getCompoundTag(NBT_FLUID_TAG);
        FluidStack fs = FluidStack.loadFluidStackFromNBT(fluidTag);

        if (fs == null) {
            fs = new FluidStack(resource, 0);
        }

        if (!fs.isFluidEqual(resource)) {
            return 0;
        }

        int amount = Math.min(CAPACITY - fs.amount, resource.amount);
        if (doFill && amount > 0) {
            fs.amount += amount;
            fs.writeToNBT(fluidTag);
            tagCompound.setTag(NBT_FLUID_TAG, fluidTag);
        }
        return amount;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackSize != 1) {
            return null;
        }

        NBTTagCompound tagCompound = getOrCreateNbtData(container);
        NBTTagCompound fluidTag = tagCompound.getCompoundTag(NBT_FLUID_TAG);
        FluidStack fs = FluidStack.loadFluidStackFromNBT(fluidTag);
        if (fs == null) {
            return null;
        }

        maxDrain = Math.min(fs.amount, maxDrain);
        if (doDrain) {
            fs.amount -= maxDrain;

            if (fs.amount <= 0) {
                tagCompound.removeTag(NBT_FLUID_TAG);
            } else {
                fs.writeToNBT(fluidTag);
                tagCompound.setTag(NBT_FLUID_TAG, fluidTag);
            }
        }
        return new FluidStack(fs, maxDrain);
    }

    /*
     * IFluidContainerItem end
     */

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advancedTooltip) {
        tooltip.addAll(getToolTipInfo(stack));
    }

    public static List<String> getToolTipInfo(ItemStack stack) {
        List<String> list = new ArrayList<>();

        FluidStack fluid = ((IFluidContainerItem) GraviSuiteNeoRegistry.itemPlasmaCell).getFluid(stack);
        if (fluid != null) {
            list.add(
                    StatCollector.translateToLocalFormatted(
                            "message.plasmaCell.contains",
                            FluidHelper.getFluidName(fluid),
                            fluid.amount));
            list.add(
                    StatCollector.translateToLocalFormatted(
                            "message.plasmaCell.efficiency",
                            GraviSuiteNeoRegistry.getPlasmaEfficiency(fluid)));
        } else {
            list.add(StatCollector.translateToLocal("message.plasmaCell.nothing"));
            list.add(StatCollector.translateToLocalFormatted("message.plasmaCell.efficiency", 0.0f));
        }

        return list;
    }

    /**
     * We do our own DurabilityBar thing
     */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    // @Override
    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.uncommon;
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return true;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return this.maxCharge;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return this.tier;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return this.transferLimit;
    }

    @Override
    public boolean onTick(EntityPlayer player, ItemStack itemStack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> subItems) {
        final ItemStack stack = new ItemStack(this, 1);
        ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
        final ItemStack fullStack = new ItemStack(this, 1, this.getMaxDamage());
        subItems.add(fullStack);

        for (Fluid fluid : GraviSuiteNeoRegistry.getRegisteredFuels()) {
            final ItemStack tempStack = new ItemStack(this, 1, this.getMaxDamage());
            ElectricItem.manager.charge(tempStack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
            this.fill(tempStack, new FluidStack(fluid, CAPACITY), true);
            subItems.add(tempStack);
        }
    }

    @Override
    public int getNumBars(ItemStack itemStack) {
        return 2;
    }

    @Override
    public Color getColorForMinValue(ItemStack itemStack, int barIndex) {
        return Color.RED;
    }

    @Override
    public Color getColorForMaxValue(ItemStack itemStack, int barIndex) {
        if (barIndex == 0) {
            return Color.GREEN;
        }
        return Color.YELLOW;
    }

    @Override
    public double getMaxValue(ItemStack itemStack, int barIndex) {
        if (barIndex == 0) {
            return this.maxCharge;
        }
        return CAPACITY;
    }

    @Override
    public double getValueForBar(ItemStack itemStack, int barIndex) {
        if (barIndex == 0) {
            return ElectricItem.manager.getCharge(itemStack);
        }
        FluidStack tFluid = this.getFluid(itemStack);
        if (tFluid != null) {
            return tFluid.amount;
        }
        return 0;
    }

    @Override
    public BarAlignment getBarAlignment() {
        return BarAlignment.BOTTOM;
    }

    @Override
    public int getBarThickness(ItemStack itemStack, int barIndex) {
        return 1;
    }

    @Override
    public boolean getIsBarInverted(ItemStack itemStack, int barIndex) {
        return true;
    }
}
