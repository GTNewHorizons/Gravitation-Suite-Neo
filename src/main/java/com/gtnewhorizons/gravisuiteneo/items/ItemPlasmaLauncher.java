package com.gtnewhorizons.gravisuiteneo.items;

import java.awt.Color;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import com.gtnewhorizons.gravisuiteneo.client.ICustomItemBars;
import com.gtnewhorizons.gravisuiteneo.common.EntityPlasmaBallMKII;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.inventory.InventoryItem;

import gravisuite.EntityPlasmaBall;
import gravisuite.GraviSuite;
import gravisuite.ServerProxy;
import ic2.api.item.ElectricItem;

public class ItemPlasmaLauncher extends Item implements ICustomItemBars {

    public static int powerPerMaxShot;
    private static final int PLASMA_AMOUNT_PER_SHOT = 1000;

    public ItemPlasmaLauncher() {
        powerPerMaxShot = Properties.ElectricPresets.PlasmaLauncher.energyPerOperation;
        this.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        this.setMaxStackSize(1);
        this.setTextureName(GraviSuiteNeo.MODID + ":PlasmaCannon_0");
        this.setUnlocalizedName("plasmaLauncher");
    }

    @Override
    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        return 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            if (player.isSneaking()) {
                player.openGui(
                        GraviSuite.instance,
                        Properties.GUIID_PLASMALAUNCHER,
                        worldIn,
                        (int) player.posX,
                        (int) player.posY,
                        (int) player.posZ);
                return itemStackIn;
            }
        }

        ItemStack ammoStack = this.getAmmunition(itemStackIn);

        if (ammoStack == null) {
            ServerProxy.sendPlayerMessage(
                    player,
                    EnumChatFormatting.RED
                            + StatCollector.translateToLocal("message.plasmaLauncher.noPlasmaCellFound"));
            return itemStackIn;
        }

        FluidStack plasma = ((ItemPlasmaCell) GraviSuiteNeoRegistry.itemPlasmaCell).getFluid(ammoStack);
        if (plasma == null) {
            ServerProxy.sendPlayerMessage(
                    player,
                    EnumChatFormatting.RED + StatCollector.translateToLocal("message.plasmaLauncher.PlasmaCellEmpty"));
            return itemStackIn;
        }
        if (plasma.amount < PLASMA_AMOUNT_PER_SHOT) {
            ServerProxy.sendPlayerMessage(
                    player,
                    EnumChatFormatting.RED + StatCollector.translateToLocal("message.plasmaLauncher.NotEnoughPlasma"));
            return itemStackIn;
        }

        ((ItemPlasmaCell) GraviSuiteNeoRegistry.itemPlasmaCell).drain(ammoStack, PLASMA_AMOUNT_PER_SHOT, true);

        float plasmaEnergyAmount = GraviSuiteNeoRegistry.getPlasmaEfficiency(plasma) / 100;
        plasmaEnergyAmount = Math.min(100.0F, plasmaEnergyAmount); // Cap to 100x

        if (ElectricItem.manager.canUse(ammoStack, powerPerMaxShot)) {
            final EntityPlasmaBall plasmaBall = new EntityPlasmaBallMKII(worldIn, player, plasmaEnergyAmount, (byte) 2);

            if (!worldIn.isRemote) {
                ElectricItem.manager.discharge(ammoStack, powerPerMaxShot, 99, true, false, false);
                worldIn.spawnEntityInWorld(plasmaBall);
                // Play Plasmagun fire-sound
                notifyWorldPlasmaFired(player);
            }
            player.swingItem();
        } else {
            ServerProxy.sendPlayerMessage(
                    player,
                    EnumChatFormatting.RED + StatCollector.translateToLocal("message.plasmaLauncher.noEnergy"));
        }

        return itemStackIn;
    }

    private static void notifyWorldPlasmaFired(EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, GraviSuiteNeo.MODID + ":plasmaFired", 1.25F, 1.0F);
    }

    public ItemStack getAmmunition(ItemStack itemStack) {
        ItemStack tReturn = null;

        InventoryItem invi = new InventoryItem(itemStack);
        ItemStack ammoStack = invi.getStackInSlot(0);
        if (ammoStack != null) {
            tReturn = ammoStack;
        }

        return tReturn;
    }

    public boolean hasAmmunition(ItemStack itemStack) {
        return this.getAmmunition(itemStack) != null;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltip,
            boolean advancedTooltips) {
        if (this.hasAmmunition(itemStack)) {
            tooltip.addAll(ItemPlasmaCell.getToolTipInfo(this.getAmmunition(itemStack)));
        } else {
            tooltip.add(StatCollector.translateToLocal("message.plasmaLauncher.depleted"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.epic;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public int getDamage(ItemStack stack) {
        stack.setItemDamage(0);
        return 0;
    }

    @Override
    public int getNumBars(ItemStack itemStack) {
        return this.hasAmmunition(itemStack) ? 2 : 0;
    }

    @Override
    public Color getColorForMinValue(ItemStack itemStack, int barIndex) {
        return ((ICustomItemBars) GraviSuiteNeoRegistry.itemPlasmaCell)
                .getColorForMinValue(this.getAmmunition(itemStack), barIndex);
    }

    @Override
    public Color getColorForMaxValue(ItemStack itemStack, int barIndex) {
        return ((ICustomItemBars) GraviSuiteNeoRegistry.itemPlasmaCell)
                .getColorForMaxValue(this.getAmmunition(itemStack), barIndex);
    }

    @Override
    public double getMaxValue(ItemStack itemStack, int barIndex) {
        return ((ICustomItemBars) GraviSuiteNeoRegistry.itemPlasmaCell)
                .getMaxValue(this.getAmmunition(itemStack), barIndex);
    }

    @Override
    public double getValueForBar(ItemStack itemStack, int barIndex) {
        return ((ICustomItemBars) GraviSuiteNeoRegistry.itemPlasmaCell)
                .getValueForBar(this.getAmmunition(itemStack), barIndex);
    }

    @Override
    public BarAlignment getBarAlignment() {
        return ((ICustomItemBars) GraviSuiteNeoRegistry.itemPlasmaCell).getBarAlignment();
    }

    @Override
    public int getBarThickness(ItemStack itemStack, int barIndex) {
        return ((ICustomItemBars) GraviSuiteNeoRegistry.itemPlasmaCell)
                .getBarThickness(this.getAmmunition(itemStack), barIndex);
    }

    @Override
    public boolean getIsBarInverted(ItemStack itemStack, int barIndex) {
        return ((ICustomItemBars) GraviSuiteNeoRegistry.itemPlasmaCell)
                .getIsBarInverted(this.getAmmunition(itemStack), barIndex);
    }
}
