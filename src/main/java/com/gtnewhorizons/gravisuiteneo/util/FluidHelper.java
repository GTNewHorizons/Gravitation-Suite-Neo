package com.gtnewhorizons.gravisuiteneo.util;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

public class FluidHelper {

    public static String getFluidName(FluidStack pFluid) {
        if (pFluid == null || pFluid.getFluid() == null) {
            return StatCollector.translateToLocal("fluid.null");
        }

        String tLocalizedFluidName = pFluid.getFluid().getLocalizedName(pFluid);
        if (tLocalizedFluidName == null) {
            return StatCollector.translateToLocal("fluid.name.null");
        }

        if (tLocalizedFluidName.equals("")) {
            tLocalizedFluidName = StatCollector.translateToLocal("fluid.name.empty");
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
