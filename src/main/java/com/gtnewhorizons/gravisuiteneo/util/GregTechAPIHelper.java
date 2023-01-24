package com.gtnewhorizons.gravisuiteneo.util;

import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.api.util.GT_Utility;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

public class GregTechAPIHelper {

    public static Map<Fluid, Integer> getGTPlasmaFluids() {
        Collection<GT_Recipe> mRecipeList = GT_Recipe_Map.sPlasmaFuels.mRecipeList;
        return mRecipeList == null
                ? Collections.emptyMap()
                : mRecipeList.parallelStream()
                        .map(GregTechAPIHelper::getFluidAndValue)
                        .filter(p -> p.getLeft() != null)
                        .collect(Collectors.toMap(p -> p.getLeft().getFluid(), Pair::getRight));
    }

    private static Pair<FluidStack, Integer> getFluidAndValue(GT_Recipe recipe) {
        return Pair.of(GT_Utility.getFluidForFilledItem(recipe.getRepresentativeInput(0), true), recipe.mSpecialValue);
    }
}
