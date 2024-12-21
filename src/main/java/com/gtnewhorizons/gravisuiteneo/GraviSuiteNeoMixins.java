package com.gtnewhorizons.gravisuiteneo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.common.FMLCommonHandler;

@LateMixin
public class GraviSuiteNeoMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.gravisuiteneo.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        mixins.add("MixinEntityPlasmaBall");
        mixins.add("MixinGraviSuite");
        mixins.add("MixinHelpers");
        mixins.add("MixinItemAdvancedJetPack");
        mixins.add("MixinItemAdvancedLappack");
        mixins.add("MixinItemAdvChainsaw");
        mixins.add("MixinItemAdvDDrill");
        mixins.add("MixinItemAdvancedNanoChestPlate");
        mixins.add("MixinItemGraviChestPlate");
        mixins.add("MixinItemRelocator");
        mixins.add("MixinItemUltimateLappack");
        mixins.add("MixinItemVajra");
        mixins.add("MixinPacketHandler");
        mixins.add("MixinKeyboard");

        if (FMLCommonHandler.instance().getSide().isClient()) {
            mixins.add("MixinBlockRelocatorPortal");
            mixins.add("MixinClientProxy");
            mixins.add("MixinClientTickHandler");
            mixins.add("MixinGuiHandler");
            mixins.add("MixinGuiRelocatorDisplay");
            mixins.add("MixinItemSimpleItems");
            mixins.add("MixinKeyboardClient");
            mixins.add("MixinRenderPlasmaBall");
        }

        return mixins;
    }
}
