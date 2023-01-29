package com.gtnewhorizons.gravisuiteneo.util;

import net.minecraftforge.fluids.FluidStack;

public class FluidHelper {

    public static String getFluidName(FluidStack pFluid) {
        if (pFluid == null || pFluid.getFluid() == null) {
            return "Error.1";
        }

        String tLocalizedFluidName = pFluid.getFluid().getLocalizedName(pFluid);
        if (tLocalizedFluidName == null) {
            return "Error.2";
        }

        if (tLocalizedFluidName.equals("")) {
            tLocalizedFluidName = "Unknown Fluid";
        }

        if (tLocalizedFluidName.equals(pFluid.getFluid().getUnlocalizedName())) {
            tLocalizedFluidName = pFluid.getFluid().getName();
            if (tLocalizedFluidName.equals(tLocalizedFluidName.toLowerCase())) {
                tLocalizedFluidName = tLocalizedFluidName.substring(0, 1).toUpperCase()
                        + tLocalizedFluidName.substring(1);
            }
        }

        return tLocalizedFluidName;
    }
}
