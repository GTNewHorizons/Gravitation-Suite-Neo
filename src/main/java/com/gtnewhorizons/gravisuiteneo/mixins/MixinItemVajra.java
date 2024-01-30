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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

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

    @SideOnly(Side.CLIENT)
    @Inject(
            at = @At(remap = false, target = "Ljava/util/List;add(Ljava/lang/Object;)Z", value = "INVOKE"),
            method = "addInformation")
    private void gravisuiteneo$addSilktouchInformation(ItemStack itemstack, EntityPlayer player, List<String> tooltip,
            boolean advancedTooltips, CallbackInfo ci) {
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
    private void gravisuiteneo$onBlockHarvestToNoOp(Block block, World world, int x, int y, int z, int meta,
            EntityPlayer player) {
        return;
    }

    @Redirect(
            method = "onItemUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"))
    private boolean gravisuiteneo$setBlockToAirToNoOp(World world, int x, int y, int z) {
        return true;
    }

    // harvestBlock would be called first by the vajra, so we redirect to the proper chain of calls
    @Redirect(
            method = "onItemUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;IIII)V"))
    private void gravisuiteneo$fixCallOrder(Block block, World world, EntityPlayer player, int x, int y, int z,
            int meta) {
        block.onBlockHarvested(world, x, y, z, meta, player);
        if (block.removedByPlayer(world, player, x, y, z, true)) {
            block.onBlockDestroyedByPlayer(world, x, y, z, meta);
            block.harvestBlock(world, player, x, y, z, meta);
        }
    }

    // This one makes sure that if we're mining a block that canSilkHarvest it still gets set to air, since we yeeted
    // the original setBlockToAir call above. This injects at the end of the if(canSilkHarvest) block, at the assignment
    // dropFlag = true
    @Inject(
            method = "onItemUse",
            at = @At(value = "INVOKE", ordinal = 1, target = "Ljava/lang/Boolean;valueOf(Z)Ljava/lang/Boolean;"))
    private void gravisuiteneo$setToAir(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        world.setBlockToAir(x, y, z);
    }

    // This mixin deals with the case that the hardness of a block is -1 (unbreakable) this should fix any exploits
    // relating to vajra using silktouch mode to break unbreakable blocks. This is maybe not the best way to do this,
    // but should be safe as far as I know.
    @ModifyExpressionValue(
            method = "onItemUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;canHarvestBlock(Lnet/minecraft/entity/player/EntityPlayer;I)Z"))
    private boolean gravisuiteneo$checkHardness(boolean canHarvest, ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float j, float k, float l, @Local(ordinal = 0) Block targetBlock) {
        return targetBlock.getBlockHardness(world, x, y, z) != -1.0F && canHarvest;

    }
}
