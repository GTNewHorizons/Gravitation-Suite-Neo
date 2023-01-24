package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.common.Achievements;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.gtnewhorizons.gravisuiteneo.util.LevelableToolHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.GraviSuite;
import gravisuite.Helpers;
import gravisuite.ItemAdvDDrill;
import gravisuite.ServerProxy;
import ic2.api.item.ElectricItem;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemAdvDDrill.class)
public abstract class MixinItemAdvDDrill extends ItemTool {

    @Unique
    private final int areYouSerious = Properties.ElectricPresets.AdvDrill.energyPerOperation * 2;

    @Unique
    private final int worldShatterer = Properties.ElectricPresets.AdvDrill.energyPerOperation * 4;

    @Shadow(remap = false)
    private float bigHolePower;

    @Shadow(remap = false)
    private float normalPower;

    @Shadow(remap = false)
    private float lowPower;

    @Shadow(remap = false)
    private float ultraLowPower;

    @Shadow(remap = false)
    private int energyPerOperation;

    @Shadow(remap = false)
    private static Material[] materials;

    @Shadow(remap = false)
    public abstract void saveToolMode(ItemStack itemstack, Integer toolMode);

    @ModifyConstant(constant = @Constant(floatValue = 1.0f, ordinal = 0), method = "getDigSpeed", remap = false)
    private float gravisuiteneo$getDigSpeedUncharged(float original) {
        return 0.0f;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return super.getHarvestLevel(stack, toolClass);
    }

    @Inject(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lgravisuite/ItemAdvDDrill;readToolMode(Lnet/minecraft/item/ItemStack;)Ljava/lang/Integer;",
                            value = "INVOKE"),
            cancellable = true,
            method = "onBlockDestroyed")
    private void gravisuiteneo$onBlockDestroyedExitEarly(
            ItemStack itemstack,
            World world,
            Block block,
            int xPos,
            int yPos,
            int zPos,
            EntityLivingBase entityliving,
            CallbackInfoReturnable<Boolean> cir) {
        if (!(entityliving instanceof EntityPlayer)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            at =
                    @At(
                            ordinal = 0,
                            remap = false,
                            target =
                                    "Lic2/api/item/IElectricItemManager;use(Lnet/minecraft/item/ItemStack;DLnet/minecraft/entity/EntityLivingBase;)Z",
                            value = "INVOKE"),
            method = "onBlockDestroyed")
    private void gravisuiteneo$addXpMode0(
            ItemStack itemstack,
            World world,
            Block block,
            int xPos,
            int yPos,
            int zPos,
            EntityLivingBase entityliving,
            CallbackInfoReturnable<Boolean> cir) {
        LevelableToolHelper.AddXP((EntityPlayer) entityliving, itemstack, 1);
    }

    @Inject(
            at =
                    @At(
                            ordinal = 1,
                            remap = false,
                            target =
                                    "Lic2/api/item/IElectricItemManager;use(Lnet/minecraft/item/ItemStack;DLnet/minecraft/entity/EntityLivingBase;)Z",
                            value = "INVOKE"),
            method = "onBlockDestroyed")
    private void gravisuiteneo$addXpMode1(
            ItemStack itemstack,
            World world,
            Block block,
            int xPos,
            int yPos,
            int zPos,
            EntityLivingBase entityliving,
            CallbackInfoReturnable<Boolean> cir) {
        if (GraviSuite.random.nextInt(2) == 0) {
            LevelableToolHelper.AddXP((EntityPlayer) entityliving, itemstack, 1);
        }
    }

    @Inject(
            at =
                    @At(
                            ordinal = 2,
                            remap = false,
                            target =
                                    "Lic2/api/item/IElectricItemManager;use(Lnet/minecraft/item/ItemStack;DLnet/minecraft/entity/EntityLivingBase;)Z",
                            value = "INVOKE"),
            method = "onBlockDestroyed")
    private void gravisuiteneo$addXpMode2(
            ItemStack itemstack,
            World world,
            Block block,
            int xPos,
            int yPos,
            int zPos,
            EntityLivingBase entityliving,
            CallbackInfoReturnable<Boolean> cir) {
        if (GraviSuite.random.nextInt(4) == 0) {
            LevelableToolHelper.AddXP((EntityPlayer) entityliving, itemstack, 1);
        }
    }

    @ModifyConstant(constant = @Constant(intValue = 3), method = "readToolMode", remap = false)
    private static int gravisuiteneo$getMaxToolMode(int original) {
        return 5;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!gravisuite.keyboard.Keyboard.isModeKeyDown(player)) {
            return itemStackIn;
        }

        int toolMode = ItemAdvDDrill.readToolMode(itemStackIn);
        toolMode++;
        if (toolMode > 5) {
            toolMode = 0;
        }

        if (toolMode == 4 && !LevelableToolHelper.hasLevel(itemStackIn, 1)) {
            toolMode = 0;
        }

        if (toolMode == 5 && !LevelableToolHelper.hasLevel(itemStackIn, 2)) {
            toolMode = 0;
        }

        this.saveToolMode(itemStackIn, toolMode);

        StringBuilder message = new StringBuilder();
        message.append(EnumChatFormatting.GREEN);
        message.append(StatCollector.translateToLocal("message.text.mode"));
        message.append(": ");

        switch (toolMode) {
            case 0:
                message.append(EnumChatFormatting.GREEN);
                message.append(StatCollector.translateToLocal("message.advDDrill.mode.normal"));
                this.efficiencyOnProperMaterial = this.normalPower;
                break;
            case 1:
                message.append(EnumChatFormatting.GOLD);
                message.append(StatCollector.translateToLocal("message.advDDrill.mode.lowPower"));
                this.efficiencyOnProperMaterial = this.lowPower;
                break;
            case 2:
                message.append(EnumChatFormatting.AQUA);
                message.append(StatCollector.translateToLocal("message.advDDrill.mode.fine"));
                this.efficiencyOnProperMaterial = this.ultraLowPower;
                break;
            case 3:
                message.append(EnumChatFormatting.LIGHT_PURPLE);
                message.append(StatCollector.translateToLocal("message.advDDrill.mode.bigHoles"));
                this.efficiencyOnProperMaterial = this.bigHolePower;
                break;
            case 4:
                message.append(EnumChatFormatting.RED);
                message.append(StatCollector.translateToLocal("message.advDDrill.mode.areYouSerious"));
                this.efficiencyOnProperMaterial = this.areYouSerious;
                break;
            case 5:
                message.append(EnumChatFormatting.DARK_RED);
                message.append(StatCollector.translateToLocal("message.advDDrill.mode.worldShatterer"));
                this.efficiencyOnProperMaterial = this.worldShatterer;
                break;

            default:
                break;
        }

        ServerProxy.sendPlayerMessage(player, message.toString());

        return itemStackIn;
    }

    @SuppressWarnings("unchecked")
    @Inject(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lgravisuite/ItemAdvDDrill;readToolMode(Lnet/minecraft/item/ItemStack;)Ljava/lang/Integer;",
                            value = "INVOKE"),
            method = "addInformation")
    @SideOnly(Side.CLIENT)
    private void gravisuiteneo$addXPInformation(
            ItemStack itemstack,
            EntityPlayer player,
            @SuppressWarnings("rawtypes") List tooltip,
            boolean advancedTooltip,
            CallbackInfo ci) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            return;
        }

        int currentTotalXP = (int) LevelableToolHelper.readToolXP(itemstack);
        int currentLevel = LevelableToolHelper.getLevel(itemstack);

        if (currentLevel < 2) {
            tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("message.xp.youhavecollected") + ": "
                    + EnumChatFormatting.WHITE + currentTotalXP);
            tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("message.xp.nextLevelUp") + ": "
                    + EnumChatFormatting.WHITE + LevelableToolHelper.getXPForLevel(currentLevel + 1));
            tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("message.xp.toolLevel") + ": "
                    + EnumChatFormatting.WHITE + currentLevel);
        } else {
            tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("message.xp.maxLevelReached"));
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, method = "addInformation")
    @SideOnly(Side.CLIENT)
    private void gravisuiteneo$addOtherModeInformation(
            ItemStack itemstack,
            EntityPlayer player,
            @SuppressWarnings("rawtypes") List tooltip,
            boolean advancedTooltips,
            CallbackInfo ci,
            Integer toolMode) {
        if (toolMode == 4) {
            tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("message.text.mode") + ": "
                    + EnumChatFormatting.WHITE
                    + StatCollector.translateToLocal("message.advDDrill.mode.areYouSerious"));
            return;
        }
        if (toolMode == 5) {
            tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("message.text.mode") + ": "
                    + EnumChatFormatting.WHITE
                    + StatCollector.translateToLocal("message.advDDrill.mode.worldShatterer"));
        }
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack itemstack) {
        switch (LevelableToolHelper.getLevel(itemstack)) {
            case 1:
                return EnumRarity.rare;
            case 2:
                return EnumRarity.epic;
            default:
                return EnumRarity.uncommon;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
        final int toolMode = ItemAdvDDrill.readToolMode(itemstack);
        if (toolMode >= 3) {
            int tTotalToolXPGain = 0;
            boolean canCollectXP = !this.hasMaxLevel(itemstack);

            final World world = player.worldObj;
            final Block block = world.getBlock(X, Y, Z);
            final int meta = world.getBlockMetadata(X, Y, Z);
            boolean lowPower = false;

            if (block == null) {
                return super.onBlockStartBreak(itemstack, X, Y, Z, player);
            }

            final float blockHardness = block.getBlockHardness(world, X, Y, Z);

            boolean validStart = false;
            for (final Material material : materials) {
                if (material == block.getMaterial()) {
                    validStart = true;
                    break;
                }
            }

            if (block == Blocks.monster_egg) {
                validStart = true;
            }
            final MovingObjectPosition mop = ItemAdvDDrill.raytraceFromEntity(world, player, true, 4.5D);

            if (mop == null || !validStart) {
                return super.onBlockStartBreak(itemstack, X, Y, Z, player);
            }

            int xRange = 1 + (toolMode - 3) * 2;
            int yRange = 1 + (toolMode - 3) * 2;
            int zRange = 1 + (toolMode - 3) * 2;
            switch (mop.sideHit) {
                case 0:
                case 1:
                    yRange = 0;
                    break;
                case 2:
                case 3:
                    zRange = 0;
                    break;
                case 4:
                case 5:
                    xRange = 0;
            }

            int tFinalEnergyPerOperation = this.energyPerOperation;
            if (toolMode == 4) {
                tFinalEnergyPerOperation = this.areYouSerious;
            } else if (toolMode == 5) {
                tFinalEnergyPerOperation = this.worldShatterer;
            }

            /*Neat idea, but annoying as hell...
             * if (ElectricItem.manager.canUse(stack, tFinalEnergyPerOperation * 10))
            {
                PlayRandomDrillSound(player);
            }*/

            final int fortune = EnchantmentHelper.getFortuneModifier(player);
            for (int xPos = X - xRange; xPos <= X + xRange; xPos++) {
                for (int yPos = Y - yRange; yPos <= Y + yRange; yPos++) {
                    for (int zPos = Z - zRange; zPos <= Z + zRange; zPos++) {
                        if (ElectricItem.manager.canUse(itemstack, tFinalEnergyPerOperation)) {
                            final Block localBlock = world.getBlock(xPos, yPos, zPos);
                            final int localMeta = world.getBlockMetadata(xPos, yPos, zPos);

                            final boolean canHarvest = this.canHarvestBlock(block, itemstack);

                            final float localHardness = localBlock == null
                                    ? Float.MAX_VALUE
                                    : localBlock.getBlockHardness(world, xPos, yPos, zPos);
                            if (canHarvest) {
                                if (localBlock != null && localHardness >= 0.0F) {
                                    for (final Material material : materials) {
                                        if (material == localBlock.getMaterial()
                                                || localBlock == Blocks.monster_egg) {
                                            if (!player.capabilities.isCreativeMode) {
                                                if (localBlock.removedByPlayer(
                                                        world, player, xPos, yPos, zPos, true)) {
                                                    localBlock.onBlockDestroyedByPlayer(
                                                            world, xPos, yPos, zPos, localMeta);
                                                }

                                                final int exp = localBlock.getExpDrop(world, localMeta, fortune);

                                                // Suck up any XP dropped from blocks, but add at least 1
                                                if (!canCollectXP) {
                                                    localBlock.dropXpOnBlockBreak(world, xPos, yPos, zPos, exp);
                                                } else {
                                                    tTotalToolXPGain += Math.max(exp, 1);
                                                }

                                                localBlock.harvestBlock(world, player, xPos, yPos, zPos, localMeta);
                                                localBlock.onBlockHarvested(
                                                        world, xPos, yPos, zPos, localMeta, player);

                                                if (blockHardness > 0.0F) {
                                                    this.onBlockDestroyed(
                                                            itemstack, world, localBlock, xPos, yPos, zPos, player);
                                                }

                                                world.func_147479_m(X, Y, Z);
                                                ElectricItem.manager.use(
                                                        itemstack, tFinalEnergyPerOperation, player);
                                            } else {
                                                Helpers.setBlockToAir(world, xPos, yPos, zPos);
                                                world.func_147479_m(X, Y, Z);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            lowPower = true;
                        }
                    }
                }
            }

            LevelableToolHelper.AddXP(player, itemstack, tTotalToolXPGain);
            if (canCollectXP && LevelableToolHelper.getLevel(itemstack) == 2) {
                Achievements.POWERDRILL_MARKIII.triggerAchievement(player);
            }

            if (!GraviSuite.isSimulating()) {
                world.playAuxSFX(2001, X, Y, Z, Block.getIdFromBlock(block) + (meta << 12));
            }

            if (lowPower) {
                ServerProxy.sendPlayerMessage(player, "Not enough energy to complete this operation !");
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
        int level = LevelableToolHelper.getLevel(stack);
        float attackDamage = 1.0f;
        if (level == 1) {
            attackDamage = 10.0f;
        } else if (level == 2) {
            attackDamage = 30.0f;
        }

        if (ElectricItem.manager.use(stack, this.energyPerOperation * 10, p_77644_3_)) {
            p_77644_2_.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) p_77644_3_), attackDamage);
        } else {
            p_77644_2_.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) p_77644_3_), 1.0f);
        }

        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        switch (LevelableToolHelper.getLevel(stack)) {
            case 2:
                return "item.advDDrill_level2";
            case 3:
                return "item.advDDrill_level3";
            default:
                return "item.advDDrill";
        }
    }

    public boolean hasMaxLevel(ItemStack pItemStack) {
        return LevelableToolHelper.getLevel(pItemStack) >= 2;
    }

    public void setXP(ItemStack pItemStack, double pXP) {
        LevelableToolHelper.saveToolXP(pItemStack, Math.min(LevelableToolHelper.getXPForLevel(2), pXP));
    }

    public void setLevel(ItemStack pItemStack, int pLevel) {
        LevelableToolHelper.saveToolXP(pItemStack, LevelableToolHelper.getXPForLevel(Math.min(2, pLevel)));
    }

    private MixinItemAdvDDrill(float p_i45333_1_, ToolMaterial p_i45333_2_, Set<?> p_i45333_3_) {
        super(p_i45333_1_, p_i45333_2_, p_i45333_3_);
    }
}
