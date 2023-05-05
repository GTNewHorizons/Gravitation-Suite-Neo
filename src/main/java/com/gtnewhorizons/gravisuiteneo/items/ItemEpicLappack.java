package com.gtnewhorizons.gravisuiteneo.items;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ISpecialArmor;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import com.gtnewhorizons.gravisuiteneo.common.Properties;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedLappack;
import gravisuite.ItemUltimateLappack;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;

public class ItemEpicLappack extends ItemUltimateLappack implements IElectricItem, IMetalArmor, ISpecialArmor {

    private static final Random RNG = new Random();
    private final int tickChance;

    public ItemEpicLappack() {
        super(
                ArmorMaterial.DIAMOND,
                GraviSuite.proxy.addArmor(GraviSuiteNeo.MODID),
                1,
                Properties.ElectricPresets.EpicLappack.maxCharge,
                Properties.ElectricPresets.EpicLappack.tier,
                Properties.ElectricPresets.EpicLappack.transferLimit);
        this.setCreativeTab(GraviSuiteNeoRegistry.graviCreativeTab);
        this.setUnlocalizedName("epiclappack");
        this.tickChance = Properties.AdvTweaks.getEpicLappackChargeTickChance();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(GraviSuiteNeo.MODID + ":itemEpicLappack");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return GraviSuiteNeo.MODID + ":textures/armor/armor_epic_lappack.png";
    }

    @Override
    public boolean onTick(EntityPlayer player, ItemStack itemstack) {
        super.onTick(player, itemstack);
        final int toolMode = readToolMode(itemstack);
        if (GraviSuite.isSimulating() && toolMode == 1) {
            if (RNG.nextInt(this.tickChance) == 0) {
                for (ItemStack is : player.inventory.mainInventory) {
                    if (is == null) {
                        continue;
                    }
                    if (is.getItem() instanceof IElectricItem && !(is.getItem() instanceof ItemAdvancedLappack)) {
                        ((IItemCharger) this).doChargeItemStack(itemstack, is);
                    }
                    if (is.getItem() instanceof IEnergyContainerItem) {
                        ((IItemCharger) this).doChargeItemStackRF(itemstack, is);
                    }
                }
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack var1) {
        return EnumRarity.epic;
    }
}
