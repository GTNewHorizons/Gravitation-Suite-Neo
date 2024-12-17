package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.gravisuiteneo.common.Properties;

import gravisuite.ItemAdvancedJetPack;
import gravisuite.ItemAdvancedNanoChestPlate;
import ic2.api.item.ElectricItem;

@Mixin(ItemAdvancedNanoChestPlate.class)
public class MixinItemAdvancedNanoChestPlate extends ItemAdvancedJetPack {

    @Shadow
    private static byte tickRate = 20;
    @Shadow
    private static byte ticker;
    @Shadow
    private int energyForExtinguish = 50000;

    /**
     * Ignore me, required to enable super calls.
     */
    public MixinItemAdvancedNanoChestPlate(ArmorMaterial armorMaterial, int par3, int par4) {
        super(armorMaterial, par3, par4);
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public double getDamageAbsorptionRatio() {
        return Properties.ArmorPresets.AdvNanoChestPlate.absorptionRatio;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    private double getBaseAbsorptionRatio() {
        return 1.0;
    }

    /**
     * @author YannickMG, Caedis
     * @reason The original method mistakenly consumed the wrong types of cells, and consuming water cells felt
     *         unnecessary. <br>
     *         Motivated by <a href="https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/15529">Issue
     *         #15529</a>.
     */
    @Inject(method = "onArmorTick", at = @At("HEAD"), cancellable = true, remap = false)
    void onArmorTickLenient(World worldObj, EntityPlayer player, ItemStack itemStack, CallbackInfo ci) {
        super.onArmorTick(worldObj, player, itemStack);
        byte currentTick = ticker;
        ticker = (byte) (currentTick + 1);
        if (currentTick % tickRate == 0 && player.isBurning()
                && ElectricItem.manager.canUse(itemStack, energyForExtinguish)) {
            ItemAdvancedNanoChestPlate.use(itemStack, energyForExtinguish);
            player.extinguish();
        }
        ci.cancel();
    }
}
