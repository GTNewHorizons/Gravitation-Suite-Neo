package com.gtnewhorizons.gravisuiteneo.mixins;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravisuite.ItemAdvChainsaw;
import gravisuite.ServerProxy;
import gravisuite.keyboard.Keyboard;
import ic2.api.item.ElectricItem;

@Mixin(ItemAdvChainsaw.class)
public abstract class MixinItemAdvChainsaw extends ItemTool {

    @Shadow(remap = false)
    private int energyPerOperation;

    @Shadow(remap = false)
    public Set<Block> mineableBlocks;

    @Shadow(remap = false)
    public abstract void saveToolMode(ItemStack itemstack, Integer toolMode);

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public void init() {
        for (String blockName : Properties.AdvTweaks.getAdvChainsawAdditionalMineableBlocks()) {
            String[] blockNameParts = blockName.split(":", 1);
            if (blockNameParts.length != 2) {
                continue;
            }

            Block block = GameRegistry.findBlock(blockNameParts[0], blockNameParts[1]);
            if (block == null) {
                continue;
            }

            this.mineableBlocks.add(block);
        }
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return super.getHarvestLevel(stack, toolClass);
    }

    @ModifyConstant(constant = @Constant(floatValue = 1.0f, ordinal = 0), method = "getDigSpeed", remap = false)
    private float gravisuiteneo$getDigSpeedUncharged(float original) {
        return 0.0f;
    }

    @ModifyExpressionValue(
            at = @At(remap = false, target = "Ljava/lang/Integer;intValue()I", value = "INVOKE"),
            method = "onEntityInteract",
            remap = false)
    private int gravisuiteneo$getShearMode(int original) {
        // expects 0 for shear mode but it is mode 1
        return original - 1;
    }

    @Inject(
            at = @At(
                    ordinal = 2,
                    opcode = Opcodes.GETFIELD,
                    remap = true,
                    target = "Lnet/minecraft/entity/player/EntityPlayer;worldObj:Lnet/minecraft/world/World;",
                    value = "FIELD"),
            method = "onBlockStartBreak",
            remap = false)
    private void gravisuiteneo$playChainsawSound(ItemStack itemstack, int x, int y, int z, EntityPlayer player,
            CallbackInfoReturnable<Boolean> cir) {
        player.worldObj.playSoundAtEntity(player, GraviSuiteNeo.MODID + ":chainsaw", 1.25f, 1.0f);
    }

    @Inject(at = @At(ordinal = 1, value = "RETURN"), cancellable = true, method = "onBlockStartBreak", remap = false)
    private void gravisuiteneo$handleTreeToolMode(ItemStack itemstack, int x, int y, int z, EntityPlayer player,
            CallbackInfoReturnable<Boolean> cir) {
        if (ItemAdvChainsaw.readToolMode(itemstack) != 2) {
            return;
        }
        // This code is (c) by Tinkers construct
        World world = player.worldObj;
        Block block = world.getBlock(x, y, z);
        if (block == null || !ElectricItem.manager.canUse(itemstack, this.energyPerOperation * 10)) {
            cir.setReturnValue(true);
            return;
        }
        if ((block.isWood(world, x, y, z) || block.getMaterial() == Material.sponge)
                && this.detectTree(world, x, y, z, block)) {
            this.breakTree(world, x, y, z, x, y, z, itemstack, block, world.getBlockMetadata(x, y, z), player);
            cir.setReturnValue(true);
        }
    }

    @ModifyConstant(constant = @Constant(intValue = 1), method = "readToolMode", remap = false)
    private static int gravisuiteneo$getMaxToolMode1(int original) {
        return 2;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (Keyboard.isModeKeyDown(player)) {
            int toolMode = ItemAdvChainsaw.readToolMode(itemStack);

            toolMode++;
            if (toolMode > 2) {
                toolMode = 0;
            }
            this.saveToolMode(itemStack, toolMode);

            StringBuilder message = new StringBuilder();
            message.append(EnumChatFormatting.GREEN);
            message.append(StatCollector.translateToLocal("message.text.mode"));
            message.append(": ");
            if (toolMode == 0) {
                message.append(EnumChatFormatting.GREEN);
                message.append(StatCollector.translateToLocal("message.advChainsaw.mode.axe"));
            } else if (toolMode == 1) {
                message.append(EnumChatFormatting.GOLD);
                message.append(StatCollector.translateToLocal("message.advChainsaw.mode.shear"));
            } else if (toolMode == 2) {
                message.append(EnumChatFormatting.AQUA);
                message.append(StatCollector.translateToLocal("message.advChainsaw.mode.treecapitator"));
            }

            ServerProxy.sendPlayerMessage(player, message.toString());
        }

        return itemStack;
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack item, EntityPlayer player, List<String> tooltip, boolean advancedTooltips) {
        final int toolMode = ItemAdvChainsaw.readToolMode(item);
        StringBuilder line = new StringBuilder();
        line.append(EnumChatFormatting.GOLD);
        line.append(StatCollector.translateToLocal("message.text.mode"));
        line.append(": ");
        line.append(EnumChatFormatting.WHITE);
        if (toolMode == 0) {
            line.append(StatCollector.translateToLocal("message.advChainsaw.mode.axe"));
        } else if (toolMode == 1) {
            line.append(StatCollector.translateToLocal("message.advChainsaw.mode.shear"));
        } else if (toolMode == 2) {
            line.append(StatCollector.translateToLocal("message.advChainsaw.mode.treecapitator"));
        }
        tooltip.add(line.toString());
    }

    /**
     * This code is (c) by Tinkers construct
     */
    @Unique
    private void breakTree(World world, int x, int y, int z, int xStart, int yStart, int zStart, ItemStack stack,
            Block block, int meta, EntityPlayer player) {
        for (int xPos = x - 1; xPos <= x + 1; xPos++) {
            for (int yPos = y; yPos <= y + 1; yPos++) {
                for (int zPos = z - 1; zPos <= z + 1; zPos++) {
                    if (ElectricItem.manager.use(stack, this.energyPerOperation, player)) {
                        Block localBlock = world.getBlock(xPos, yPos, zPos);
                        if (block == localBlock) {
                            int localMeta = world.getBlockMetadata(xPos, yPos, zPos);

                            boolean cancelHarvest = false;

                            // send blockbreak event
                            BreakEvent event = new BreakEvent(x, y, z, world, localBlock, localMeta, player);
                            event.setCanceled(cancelHarvest);
                            MinecraftForge.EVENT_BUS.post(event);
                            cancelHarvest = event.isCanceled();

                            int xDist = xPos - xStart;
                            int yDist = yPos - yStart;
                            int zDist = zPos - zStart;

                            if (9 * xDist * xDist + yDist * yDist + 9 * zDist * zDist < 2500) {
                                if (cancelHarvest) {
                                    this.breakTree(
                                            world,
                                            xPos,
                                            yPos,
                                            zPos,
                                            xStart,
                                            yStart,
                                            zStart,
                                            stack,
                                            block,
                                            meta,
                                            player);
                                } else {
                                    if (localMeta % 4 == meta % 4) {
                                        if (!player.capabilities.isCreativeMode) {
                                            localBlock.harvestBlock(world, player, x, y, z, localMeta);
                                            this.onBlockDestroyed(stack, world, localBlock, xPos, yPos, zPos, player);
                                        }

                                        world.setBlockToAir(xPos, yPos, zPos);
                                        if (!world.isRemote) {
                                            this.breakTree(
                                                    world,
                                                    xPos,
                                                    yPos,
                                                    zPos,
                                                    xStart,
                                                    yStart,
                                                    zStart,
                                                    stack,
                                                    block,
                                                    meta,
                                                    player);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This code is (c) by Tinkers construct
     */
    @Unique
    private boolean detectTree(World world, int x, int y, int z, Block wood) {
        int height = y;
        boolean foundTop = false;
        do {
            height++;
            Block block = world.getBlock(x, height, z);
            if (block != wood) {
                height--;
                foundTop = true;
            }
        } while (!foundTop);

        int numLeaves = 0;
        if (height - y < 50) {
            for (int xPos = x - 1; xPos <= x + 1; xPos++) {
                for (int yPos = height - 1; yPos <= height + 1; yPos++) {
                    for (int zPos = z - 1; zPos <= z + 1; zPos++) {
                        Block leaves = world.getBlock(xPos, yPos, zPos);
                        if (leaves != null && leaves.isLeaves(world, xPos, yPos, zPos)) {
                            numLeaves++;
                        }
                    }
                }
            }
        }

        return numLeaves > 3;
    }

    private MixinItemAdvChainsaw(float p_i45333_1_, ToolMaterial p_i45333_2_, Set<Block> p_i45333_3_) {
        super(p_i45333_1_, p_i45333_2_, p_i45333_3_);
    }
}
