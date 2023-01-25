package com.gtnewhorizons.gravisuiteneo.common;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import gravisuite.GraviSuite;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeHandler {

    public static void registerRecipes() {
        if (!Properties.disableNanobotRecipe) {
            Recipes.advRecipes.addRecipe(
                    new ItemStack(GraviSuite.instance.itemSimpleItem, 4, 7),
                    " C ",
                    "CMC",
                    " C ",
                    'M',
                    Items.milk_bucket,
                    'C',
                    IC2Items.getItem("advancedCircuit"));
        }

        if (!Properties.disableEpicLappackRecipe) {
            Recipes.advRecipes.addRecipe(
                    new ItemStack(GraviSuiteNeoRegistry.epicLappack),
                    "RBR",
                    "RDR",
                    "RAR",
                    'R',
                    new ItemStack(GraviSuite.ultimateLappack, 1, OreDictionary.WILDCARD_VALUE),
                    'B',
                    IC2Items.getItem("iridiumPlate"),
                    'D',
                    new ItemStack(GraviSuite.ultimateLappack, 1, OreDictionary.WILDCARD_VALUE),
                    'A',
                    "itemSuperconductor");
        }
    }
}
