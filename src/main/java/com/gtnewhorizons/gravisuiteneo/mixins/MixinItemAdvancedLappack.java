package com.gtnewhorizons.gravisuiteneo.mixins;

import cofh.api.energy.IEnergyContainerItem;
import com.gtnewhorizons.gravisuiteneo.common.Achievements;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.items.IItemCharger;
import com.gtnewhorizons.gravisuiteneo.items.ItemEpicLappack;
import gravisuite.GraviSuite;
import gravisuite.ItemAdvancedLappack;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemAdvancedLappack.class)
public class MixinItemAdvancedLappack implements IItemCharger {

    @Shadow(remap = false)
    private int transferLimit;

    @Shadow(remap = false)
    private int tier;

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public double getDamageAbsorptionRatio() {
        return Properties.ArmorPresets.AdvLapPack.absorptionRatio;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    private double getBaseAbsorptionRatio() {
        return 1.0;
    }

    @Inject(
            at =
                    @At(
                            ordinal = 1,
                            remap = false,
                            target =
                                    "Lgravisuite/ServerProxy;sendPlayerMessage(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V",
                            value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "onItemRightClick")
    private void gravisuiteneo$triggerAchievement(
            ItemStack itemStack,
            World world,
            EntityPlayer player,
            CallbackInfoReturnable<ItemStack> cir,
            Integer toolMode) {
        if (itemStack.getItem() instanceof ItemEpicLappack) {
            player.triggerAchievement(Achievements.EPIC_LAPPACK);
        }
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public boolean onTick(EntityPlayer player, ItemStack itemstack) {
        int toolMode = ItemAdvancedLappack.readToolMode(itemstack);
        if (toolMode == 1 && GraviSuite.isSimulating()) {
            ItemStack armorItemStack = player.inventory.armorInventory[2];
            if (armorItemStack == null) {
                return true;
            }
            if (armorItemStack.getItem() instanceof IElectricItem) {
                this.doChargeItemStack(itemstack, armorItemStack);
            }
            if (armorItemStack.getItem() instanceof IEnergyContainerItem) {
                this.doChargeItemStackRF(itemstack, armorItemStack);
            }
        }
        return true;
    }

    @Override
    public void doChargeItemStack(ItemStack charger, ItemStack chargee) {
        int energyPacket = this.transferLimit;
        int mainCharge = ItemAdvancedLappack.getCharge(charger);
        if (mainCharge <= this.transferLimit) {
            energyPacket = mainCharge;
        }

        double sentPacket = ElectricItem.manager.charge(chargee, energyPacket, this.tier, false, false);
        if (sentPacket > 0.0D) {
            ElectricItem.manager.discharge(charger, sentPacket, this.tier, false, false, false);
        }
    }

    @Override
    public void doChargeItemStackRF(ItemStack charger, ItemStack chargee) {
        int energyPacket = this.transferLimit;
        int mainCharge = ItemAdvancedLappack.getCharge(charger);
        if (mainCharge <= this.transferLimit) {
            energyPacket = mainCharge;
        }

        double sentPacket =
                ((IEnergyContainerItem) chargee.getItem()).receiveEnergy(chargee, energyPacket * 4, false) / 4.0D;
        if (sentPacket > 0.0D) {
            ElectricItem.manager.discharge(charger, sentPacket, this.tier, false, false, false);
        }
    }
}
