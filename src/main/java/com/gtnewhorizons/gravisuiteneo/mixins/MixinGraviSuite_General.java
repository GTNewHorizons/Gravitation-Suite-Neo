package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.gtnewhorizons.gravisuiteneo.common.EntityPlasmaBallMKII;
import com.gtnewhorizons.gravisuiteneo.common.Properties;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gravisuite.EntityPlasmaBall;
import gravisuite.GraviSuite;

@Mixin(GraviSuite.class)
public class MixinGraviSuite_General {

    private static final Logger LOGGER = LogManager.getLogger("GraviSuite");

    /**
     * @author glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public static void addLog(String logString) {
        LOGGER.info(logString);
    }

    @Inject(
            at = @At(opcode = Opcodes.PUTSTATIC, remap = false, target = "logWrench:Z", value = "FIELD"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "preInit",
            remap = false)
    private void gravisuiteneo$getProperties(FMLPreInitializationEvent event, CallbackInfo ci, Configuration config) {
        Properties.init(config);
    }

    @ModifyArgs(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/ItemUltimateLappack;<init>(Lnet/minecraft/item/ItemArmor$ArmorMaterial;IIIII)V",
                    value = "INVOKE"),
            method = "preInit",
            remap = false)
    private void gravisuiteneo$setUltimateLappackArgs(Args args) {
        args.set(3, Properties.ElectricPresets.UltimateLappack.maxCharge);
        args.set(4, Properties.ElectricPresets.UltimateLappack.tier);
        args.set(5, Properties.ElectricPresets.UltimateLappack.transferLimit);
    }

    @ModifyArgs(
            at = @At(
                    remap = false,
                    target = "Lgravisuite/ItemAdvancedLappack;<init>(Lnet/minecraft/item/ItemArmor$ArmorMaterial;IIIII)V",
                    value = "INVOKE"),
            method = "preInit",
            remap = false)
    private void gravisuiteneo$setAdvLapPackArgs(Args args) {
        args.set(3, Properties.ElectricPresets.AdvLapPack.maxCharge);
        args.set(4, Properties.ElectricPresets.AdvLapPack.tier);
        args.set(5, Properties.ElectricPresets.AdvLapPack.transferLimit);
    }

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lcpw/mods/fml/common/registry/GameRegistry;registerItem(Lnet/minecraft/item/Item;Ljava/lang/String;)V",
                    value = "INVOKE"),
            method = "preInit",
            remap = false,
            slice = @Slice(
                    from = @At(
                            opcode = Opcodes.PUTSTATIC,
                            remap = false,
                            target = "Lgravisuite/GraviSuite;sonicLauncher:Lnet/minecraft/item/Item;",
                            value = "FIELD"),
                    to = @At(
                            remap = false,
                            target = "Lgravisuite/GraviSuite;registerEntity(Ljava/lang/Class;Ljava/lang/String;)V",
                            value = "INVOKE")))
    private void gravisuiteneo$preventSonicLauncherRegistration(Item item, String name) {
        // NO-OP
    }

    @ModifyConstant(constant = @Constant(classValue = EntityPlasmaBall.class), method = "preInit", remap = false)
    private Class<?> gravisuiteneo$getEntityPlasmaBallClass(Class<?> original) {
        return EntityPlasmaBallMKII.class;
    }

    @Inject(
            at = @At(
                    remap = true,
                    target = "Lnet/minecraft/nbt/NBTTagCompound;setInteger(Ljava/lang/String;I)V",
                    value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "getOrCreateNbtData",
            remap = false)
    private static void gravisuiteneo$createToolNBT(ItemStack itemstack, CallbackInfoReturnable<NBTTagCompound> cir,
            NBTTagCompound nbttagcompound) {
        nbttagcompound.setDouble("toolXP", 0.0);
        nbttagcompound.setInteger("toolMode", 0);
    }
}
