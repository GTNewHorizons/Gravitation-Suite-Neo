package com.gtnewhorizons.gravisuiteneo.mixins;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gravisuite.EntityPlasmaBall;
import gravisuite.ItemRelocator.TeleportPoint;

@Mixin(EntityPlasmaBall.class)
public interface MixinEntityPlasmaBall {

    @Accessor(remap = false)
    EntityLivingBase getOwnerEntity();

    @Accessor(remap = false)
    double getSpeedPerTick();

    @Accessor(remap = false)
    void setMaxRange(double maxRange);

    @Accessor(remap = false)
    void setSpeedPerTick(double speedPerTick);

    @Accessor(remap = false)
    double getDischargeArmorValue();

    @Accessor(remap = false)
    void setActionType(byte actionType);

    @Accessor(remap = false)
    byte getActionType();

    @Accessor(remap = false)
    TeleportPoint getTargetTpPoint();
}
