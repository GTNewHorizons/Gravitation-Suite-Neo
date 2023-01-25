package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.common.EntityPlasmaBallMKII;
import gravisuite.EntityPlasmaBall;
import gravisuite.ItemRelocator;
import gravisuite.ItemRelocator.TeleportPoint;
import gravisuite.ServerProxy;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemRelocator.class)
public class MixinItemRelocator {

    @Shadow(remap = false)
    private double maxCharge;

    @Shadow(remap = false)
    private int energyPerDimesionTp;

    @Inject(at = @At("TAIL"), method = "<init>", remap = false)
    private void gravisuiteneo$assertCorrectEnergyPerDimesionTp(ToolMaterial toolMaterial, CallbackInfo ci) {
        if (this.energyPerDimesionTp > this.maxCharge) {
            this.energyPerDimesionTp = (int) (this.maxCharge / 2.0 - 100.0);
        }
    }

    @Redirect(
            at = @At(remap = false, target = "gravisuite/EntityPlasmaBall", value = "NEW"),
            method = "onItemRightClick")
    private EntityPlasmaBall gravisuiteneo$constructEntityPlasmaBall(
            World world, EntityLivingBase entityLiving, TeleportPoint tpPoint, byte entityType) {
        return new EntityPlasmaBallMKII(world, entityLiving, tpPoint, entityType);
    }

    @Redirect(
            at = @At(remap = false, target = "Ljava/util/List;size()I", value = "INVOKE"),
            method = "addNewTeleportPoint",
            remap = false)
    private static int gravisuiteneo$removeMemoryLimit(List<TeleportPoint> tpList) {
        return 0;
    }

    @Inject(
            at =
                    @At(
                            remap = false,
                            shift = Shift.BEFORE,
                            target =
                                    "Lgravisuite/Helpers;teleportEntity(Lnet/minecraft/entity/Entity;Lgravisuite/ItemRelocator$TeleportPoint;)Lnet/minecraft/entity/Entity;",
                            value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "teleportPlayer",
            remap = false)
    private void gravisuiteneo$sendTeleportingNowMessage(
            EntityPlayer player, ItemStack itemStack, String tpName, CallbackInfo ci, TeleportPoint point) {
        ServerProxy.sendPlayerMessage(
                player,
                EnumChatFormatting.GOLD + StatCollector.translateToLocal("") + " " + EnumChatFormatting.GREEN
                        + point.pointName);
    }
}
