package com.gtnewhorizons.gravisuiteneo.mixins;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.gravisuiteneo.common.Properties;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.ItemVajra;

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
    private void gravisuiteneo$addSilktouchInformation(ItemStack itemstack, EntityPlayer player,
            @SuppressWarnings("rawtypes") List tooltip, boolean advancedTooltips, CallbackInfo ci) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("message.vajra.clickRightForSilk"));
    }

    // The vajra onItemUse has two branches for figuring out what a block drops
    // depending on whether or not the block canSilkHarvest. Then the branches come back together and set the block
    // to air.
    // the fixCallOrder redirect handles everything, so there is no need for these calls to do anything.
    @Redirect(
            method = "onItemUse",
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lnet/minecraft/block/Block;onBlockHarvested(Lnet/minecraft/world/World;IIIILnet/minecraft/entity/player/EntityPlayer;)V"))
    private void onBlockHarvestToNoOp(Block block, World world, int x, int y, int z, int meta, EntityPlayer player) {
        return;
    }

    @Redirect(
            method = "onItemUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"))
    private boolean setBlockToAirToNoOp(World world, int x, int y, int z) {
        return true;
    }

    // harvestBlock would be called first by the vajra, so we redirect to the proper chain of calls
    @Redirect(
            method = "onItemUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;IIII)V"))
    private void fixCallOrder(Block block, World world, EntityPlayer player, int x, int y, int z, int meta) {
        block.onBlockHarvested(world, x, y, z, meta, player);
        world.setBlockToAir(x, y, z);
        block.harvestBlock(world, player, x, y, z, meta);
    }

    // This one makes sure that if we're mining a block that canSilkHarvest it still gets set to air, since we yeeted
    // the original setBlockToAir call above
    @Inject(
            method = "onItemUse",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 1,
                    target = "Ljava/lang/Boolean;valueOf(Z)Ljava/lang/Boolean;"))
    private void setToAir(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX,
            float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        world.setBlockToAir(x, y, z);
    }
}
