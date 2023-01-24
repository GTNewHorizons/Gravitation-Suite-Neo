package com.gtnewhorizons.gravisuiteneo;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.common.FMLCommonHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        mixins.add("MixinItemGraviChestPlate");
        mixins.add("MixinItemRelocator");
        mixins.add("MixinItemUltimateLappack");
        mixins.add("MixinItemVajra");
        mixins.add("MixinPacketHandler");

        if (FMLCommonHandler.instance().getSide().isClient()) {
            mixins.add("MixinGuiHandler");
            mixins.add("MixinGuiRelocatorDisplay");
            mixins.add("MixinItemSimpleItems");
            mixins.add("MixinKeyboard");
            mixins.add("MixinRenderPlasmaBall");
        }

        return mixins;
    }
}
