package com.gtnewhorizons.gravisuiteneo.util;

import java.util.Map;
import java.util.stream.Collectors;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public class GregTechAPIHelper {

    public static Map<Fluid, Integer> getGTPlasmaFluids() {
        return RecipeMaps.plasmaFuels.getAllRecipes().parallelStream().map(GregTechAPIHelper::getFluidAndValue)
                .filter(p -> p.getLeft() != null)
                .collect(Collectors.toMap(p -> p.getLeft().getFluid(), Pair::getRight));
    }

    private static Pair<FluidStack, Integer> getFluidAndValue(GTRecipe recipe) {
        return Pair.of(GTUtility.getFluidForFilledItem(recipe.getRepresentativeInput(0), true), recipe.mSpecialValue);
    }
}
