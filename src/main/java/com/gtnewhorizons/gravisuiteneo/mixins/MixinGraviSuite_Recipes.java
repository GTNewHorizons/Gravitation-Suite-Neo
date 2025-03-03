package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import com.gtnewhorizons.gravisuiteneo.common.Properties;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import gravisuite.GraviSuite;
import ic2.api.recipe.ICraftingRecipeManager;

@Mixin(GraviSuite.class)
public class MixinGraviSuite_Recipes {

    @ModifyExpressionValue(
            at = { @At(
                    ordinal = 0,
                    remap = false,
                    slice = "A",
                    target = "Lic2/api/item/IC2Items;getItem(Ljava/lang/String;)Lnet/minecraft/item/ItemStack;",
                    value = "INVOKE"),
                    @At(
                            ordinal = 1,
                            remap = false,
                            slice = "A",
                            target = "Lic2/api/item/IC2Items;getItem(Ljava/lang/String;)Lnet/minecraft/item/ItemStack;",
                            value = "INVOKE"),
                    @At(
                            ordinal = 3,
                            remap = false,
                            slice = "B",
                            target = "Lic2/api/item/IC2Items;getItem(Ljava/lang/String;)Lnet/minecraft/item/ItemStack;",
                            value = "INVOKE") },
            method = "afterModsLoaded",
            remap = false,
            slice = { @Slice(
                    from = @At(
                            ordinal = 1,
                            remap = false,
                            target = "Lcpw/mods/fml/common/registry/GameRegistry;addRecipe(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V",
                            value = "INVOKE"),
                    id = "A"),
                    @Slice(
                            from = @At(
                                    opcode = Opcodes.GETSTATIC,
                                    remap = false,
                                    target = "Lgravisuite/GraviSuite;disableAdvJetpackRecipe:Z",
                                    value = "FIELD"),
                            id = "B") })
    private ItemStack gravisuiteneo$fixDamage(ItemStack original) {
        // Must be a new ItemStack so the field in Ic2Items is not modified
        return new ItemStack(original.getItem(), 1, 1);
    }

    @WrapWithCondition(
            at = @At(
                    remap = false,
                    target = "Lcpw/mods/fml/common/registry/GameRegistry;addRecipe(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V",
                    value = "INVOKE"),
            method = "afterModsLoaded",
            remap = false)
    private boolean gravisuiteneo$enableBasicRecipes(ItemStack output, Object... params) {
        return !Properties.disableBasicRecipes;
    }

    @WrapWithCondition(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    target = "Lic2/api/recipe/ICraftingRecipeManager;addRecipe(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V",
                    value = "INVOKE"),
            method = "afterModsLoaded",
            remap = false)
    private boolean gravisuiteneo$enableBasicRecipes(ICraftingRecipeManager instance, ItemStack output,
            Object... input) {
        return !Properties.disableBasicRecipes;
    }
}
