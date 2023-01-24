package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import gravisuite.ItemSimpleItems;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSimpleItems.class)
public class MixinItemSimpleItems {

    @Shadow(remap = false)
    private IIcon[] iconsList;

    @Inject(at = @At("HEAD"), method = "registerIcons")
    private void registerAntidoteIcon(IIconRegister iconRegister, CallbackInfo ci) {
        this.iconsList = new IIcon[8];
        this.iconsList[7] = iconRegister.registerIcon(GraviSuiteNeo.MODID + ":itemAntidote");
    }
}
