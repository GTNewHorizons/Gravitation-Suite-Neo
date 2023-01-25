package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizon.mixinextras.injector.WrapWithCondition;
import com.gtnewhorizons.gravisuiteneo.common.Properties;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gravisuite.GraviSuite;
import ic2.api.recipe.ICraftingRecipeManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.libraries.org.objectweb.asm.Opcodes;

@Mixin(GraviSuite.class)
public class MixinGraviSuite {

    @Inject(
            at = @At(opcode = Opcodes.PUTSTATIC, remap = false, target = "logWrench:Z", value = "FIELD"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "preInit",
            remap = false)
    private void gravisuiteneo$getProperties(FMLPreInitializationEvent event, CallbackInfo ci, Configuration config) {
        Properties.init(config);
    }

    @ModifyArgs(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lgravisuite/ItemUltimateLappack;<init>(Lnet/minecraft/item/ItemArmor$ArmorMaterial;IIIII)V",
                            value = "INVOKE"),
            method = "preInit",
            remap = false)
    private void gravisuiteneo$setUltimateLappackArgs(Args args) {
        args.set(3, Properties.ElectricPresets.UltimateLappack.maxCharge);
        args.set(4, Properties.ElectricPresets.UltimateLappack.tier);
        args.set(5, Properties.ElectricPresets.UltimateLappack.transferLimit);
    }

    @ModifyArgs(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lgravisuite/ItemAdvancedLappack;<init>(Lnet/minecraft/item/ItemArmor$ArmorMaterial;IIIII)V",
                            value = "INVOKE"),
            method = "preInit",
            remap = false)
    private void gravisuiteneo$setAdvLapPackArgs(Args args) {
        args.set(3, Properties.ElectricPresets.AdvLapPack.maxCharge);
        args.set(4, Properties.ElectricPresets.AdvLapPack.tier);
        args.set(5, Properties.ElectricPresets.AdvLapPack.transferLimit);
    }

    @Redirect(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lcpw/mods/fml/common/registry/GameRegistry;registerItem(Lnet/minecraft/item/Item;Ljava/lang/String;)V",
                            value = "INVOKE"),
            method = "preInit",
            remap = false,
            slice =
                    @Slice(
                            from =
                                    @At(
                                            opcode = Opcodes.PUTSTATIC,
                                            remap = false,
                                            target = "Lgravisuite/GraviSuite;sonicLauncher:Lnet/minecraft/item/Item;",
                                            value = "FIELD"),
                            to =
                                    @At(
                                            remap = false,
                                            target =
                                                    "Lgravisuite/GraviSuite;registerEntity(Ljava/lang/Class;Ljava/lang/String;)V",
                                            value = "INVOKE")))
    private void gravisuiteneo$preventSonicLauncherRegistration(Item item, String name) {
        // NO-OP
    }

    @WrapWithCondition(
            at =
                    @At(
                            remap = false,
                            target =
                                    "Lcpw/mods/fml/common/registry/GameRegistry;addRecipe(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V",
                            value = "INVOKE"),
            method = "afterModsLoaded",
            remap = false)
    private boolean gravisuiteneo$enableBasicRecipes(ItemStack output, Object... params) {
        return !Properties.disableBasicRecipes;
    }

    @WrapWithCondition(
            at =
                    @At(
                            ordinal = 0,
                            remap = false,
                            target =
                                    "Lic2/api/recipe/ICraftingRecipeManager;addRecipe(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V",
                            value = "INVOKE"),
            method = "afterModsLoaded",
            remap = false)
    private boolean gravisuiteneo$enableBasicRecipes(
            ICraftingRecipeManager instance, ItemStack output, Object... input) {
        return !Properties.disableBasicRecipes;
    }

    @Inject(
            at = @At(target = "Lnet/minecraft/nbt/NBTTagCompound;setInteger(Ljava/lang/String;I)V", value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "getOrCreateNbtData",
            remap = false)
    private static void gravisuiteneo$createToolNBT(
            ItemStack itemstack, CallbackInfoReturnable<NBTTagCompound> cir, NBTTagCompound nbttagcompound) {
        nbttagcompound.setDouble("toolXP", 0.0);
        nbttagcompound.setInteger("toolMode", 0);
    }
}
