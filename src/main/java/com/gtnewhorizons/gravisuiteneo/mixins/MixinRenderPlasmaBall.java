package com.gtnewhorizons.gravisuiteneo.mixins;

import com.gtnewhorizons.gravisuiteneo.common.EntityPlasmaBallMKII;
import gravisuite.EntityPlasmaBall;
import gravisuite.Helpers;
import gravisuite.client.RenderPlasmaBall;
import java.awt.Color;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RenderPlasmaBall.class)
public abstract class MixinRenderPlasmaBall extends Render {

    @Unique
    private float scaleCore;

    @Unique
    private int impactFadeStartTicks = -1;

    @Unique
    private float sizeFadeValue = 1.0F;

    @ModifyConstant(constant = @Constant(stringValue = "advancedsolarpanel"), method = "getTextureSize", remap = false)
    private static String gravisuiteneo$getResourceDomain(String prev) {
        return "gravisuite";
    }

    @Inject(
            at =
                    @At(
                            ordinal = 0,
                            remap = false,
                            shift = Shift.BEFORE,
                            target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
                            value = "INVOKE"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            method = "renderCore",
            remap = false)
    private void gravisuiteneo$setColorOtherActionTypes(
            EntityPlasmaBall entity,
            double x,
            double y,
            double z,
            float fq,
            float pticks,
            CallbackInfo ci,
            int size1,
            int size2,
            float f1,
            float f2,
            float f3,
            float f4,
            float f5,
            float scaleCore,
            double posX,
            double posY,
            double posZ,
            Tessellator tessellator,
            Color color) {
        this.scaleCore = scaleCore;

        byte actionType = entity.getActionType();
        if (actionType != 2 && actionType != 3) {
            return;
        }

        float tCharge = MathHelper.clamp_float(((EntityPlasmaBallMKII) entity).getCharge(), 0.1f, 1.0f);
        int tGColorValueOffset = (int) ((tCharge - 0.2F) * 161); // Slide to red, the more power it has
        color = Helpers.convertRGBtoColor(252, 161 - tGColorValueOffset, 3); // Orange TODO: Add plasma-based color

        if (actionType == 2) {
            this.impactFadeStartTicks = -1;
            this.scaleCore += tCharge * 2;
        } else {
            this.scaleCore += 24;
            if (this.impactFadeStartTicks == -1) {
                this.impactFadeStartTicks = entity.ticksExisted;
            }

            int tTicksRemainingToDeath = this.impactFadeStartTicks + 10 - entity.ticksExisted;
            if (tTicksRemainingToDeath <= 0) {
                ci.cancel();
            }

            this.sizeFadeValue = 100.0F / 10.0F * tTicksRemainingToDeath / 100;
            this.scaleCore *= this.sizeFadeValue;
            if (this.scaleCore == 0.0F) {
                ci.cancel();
            }
        }
    }

    @ModifyVariable(
            at =
                    @At(
                            ordinal = 0,
                            remap = false,
                            shift = Shift.BEFORE,
                            target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
                            value = "INVOKE"),
            method = "renderCore",
            name = "scaleCore",
            remap = false)
    private float gravisuiteneo$setScaleCore(float scaleCore) {
        return this.scaleCore;
    }
}
