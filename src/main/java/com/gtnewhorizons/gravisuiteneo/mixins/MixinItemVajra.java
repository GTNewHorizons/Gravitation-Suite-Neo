package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.common.Properties;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.ItemVajra;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemVajra.class)
public class MixinItemVajra {

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return Properties.AdvTweaks.getMaxMiningLevel();
    }

    @ModifyConstant(constant = @Constant(floatValue = 1.0f, ordinal = 0), method = "getDigSpeed", remap = false)
    private float gravisuiteneo$getDigSpeedUncharged(float original) {
        return 0.0f;
    }

    @ModifyConstant(constant = @Constant(intValue = 2), method = "hitEntity")
    private int gravisuiteneo$getEnergyConsumptionFactor(int original) {
        return 3;
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Inject(
            at = @At(remap = false, target = "Ljava/util/List;add(Ljava/lang/Object;)Z", value = "INVOKE"),
            method = "addInformation")
    private void gravisuiteneo$addSilktouchInformation(
            ItemStack itemstack,
            EntityPlayer player,
            @SuppressWarnings("rawtypes") List tooltip,
            boolean advancedTooltips,
            CallbackInfo ci) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("message.vajra.clickRightForSilk"));
    }
}
