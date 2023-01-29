package com.gtnewhorizons.gravisuiteneo.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.gtnewhorizons.gravisuiteneo.common.EntityPlasmaBallMKII;

import gravisuite.EntityPlasmaBall;
import gravisuite.client.ClientProxy;

@Mixin(ClientProxy.class)
public class MixinClientProxy {

    @ModifyConstant(
            constant = @Constant(classValue = EntityPlasmaBall.class),
            method = "registerRenderers",
            remap = false)
    private Class<?> gravisuiteneo$getEntityPlasmaBallClass(Class<?> original) {
        return EntityPlasmaBallMKII.class;
    }
}
