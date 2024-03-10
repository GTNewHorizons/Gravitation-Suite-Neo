package com.gtnewhorizons.gravisuiteneo.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import gravisuite.BlockRelocatorPortal;

@Mixin(BlockRelocatorPortal.class)
public class MixinBlockRelocatorPortal {

    @ModifyArg(
            at = @At(
                    target = "Lnet/minecraft/client/renderer/texture/IIconRegister;registerIcon(Ljava/lang/String;)Lnet/minecraft/util/IIcon;",
                    value = "INVOKE"),
            method = "registerBlockIcons")
    private String gravisuiteneo$fixResourceLocation(String p_94245_1_) {
        return "gravisuite:block_side";
    }

}
