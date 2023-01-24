package com.gtnewhorizons.gravisuiteneo.common;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import gravisuite.GraviSuite;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeHandler {

    public static void registerRecipes() {
        Item itemSimpleItem = GraviSuite.instance.itemSimpleItem;

        if (!Properties.disableNanobotRecipe) {
            Recipes.advRecipes.addRecipe(
                    new ItemStack(itemSimpleItem, 4, 7),
                    " C ",
                    "CMC",
                    " C ",
                    'M',
                    Items.milk_bucket,
                    'C',
                    IC2Items.getItem("advancedCircuit"));
        }

        if (!Properties.disableBasicRecipes) {
            Recipes.advRecipes.addRecipe(
                    new ItemStack(itemSimpleItem, 3, 0),
                    "RBR",
                    "CCC",
                    "RBR",
                    'R',
                    IC2Items.getItem("advancedAlloy"),
                    'B',
                    IC2Items.getItem("iridiumPlate"),
                    'C',
                    IC2Items.getItem("carbonPlate"));
            Recipes.advRecipes.addRecipe(
                    new ItemStack(itemSimpleItem, 3, 1),
                    "RRR",
                    "CBC",
                    "RRR",
                    'R',
                    new ItemStack(itemSimpleItem),
                    'B',
                    Items.gold_ingot,
                    'C',
                    IC2Items.getItem("glassFiberCableItem"));
            Recipes.advRecipes.addRecipe(
                    new ItemStack(itemSimpleItem, 1, 2),
                    "RBR",
                    "CDC",
                    "RBR",
                    'R',
                    IC2Items.getItem("reactorCoolantSix"),
                    'B',
                    IC2Items.getItem("reactorHeatSwitchDiamond"),
                    'C',
                    IC2Items.getItem("reactorPlatingHeat"),
                    'D',
                    IC2Items.getItem("iridiumPlate"));
            Recipes.advRecipes.addRecipe(
                    new ItemStack(itemSimpleItem, 1, 3),
                    "RBR",
                    "CDC",
                    "RBR",
                    'R',
                    IC2Items.getItem("teslaCoil"),
                    'B',
                    "itemSuperconductor",
                    'C',
                    new ItemStack(itemSimpleItem, 1, 2),
                    'D',
                    IC2Items.getItem("hvTransformer"));
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
