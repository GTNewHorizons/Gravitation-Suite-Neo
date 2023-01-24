package com.gtnewhorizons.gravisuiteneo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * This code is based on Thaumcraft
 */
public class FXQuantumShield extends EntityFX {

    public enum EShieldMode {
        POWER_UP,
        POWER_DOWN,
        IMPACT
    }

    private static final ResourceLocation MDL_SHIELD =
            new ResourceLocation("gravisuite", "textures/models/qshield/shield.obj");
    private static final String SHIELD_TEXTURE_HIT = "textures/models/qshield/hit/quantumShield%d.png";
    private static final String SHIELD_TEXTURE_POWER_UP = "textures/models/qshield/up/quantumShield%d.png";
    private static final String SHIELD_TEXTURE_POWER_DOWN = "textures/models/qshield/down/quantumShield%d.png";

    private final EShieldMode shieldMode;
    private final Entity target;
    private IModelCustom model;

    public FXQuantumShield(
            World world,
            double posX,
            double posY,
            double posZ,
            Entity target,
            int age,
            float yaw,
            float pitch,
            EShieldMode mode) {
        super(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D);

        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleGravity = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;

        this.particleMaxAge = age + this.rand.nextInt(age / 2);

        this.noClip = false;

        this.setSize(0.01F, 0.01F);

        this.noClip = true;

        this.particleScale = 1.0F;

        this.target = target;
        this.prevRotationYaw = this.rotationYaw = yaw;
        this.prevRotationPitch = this.rotationPitch = pitch;

        this.prevPosX = this.posX = this.target.posX;
        this.prevPosY = this.posY = (this.target.boundingBox.minY + this.target.boundingBox.maxY) / 2.0D;
        this.prevPosZ = this.posZ = this.target.posZ;
        this.shieldMode = mode;
    }

    private static ResourceLocation getResLoc(int frame, EShieldMode mode) {
        switch (mode) {
            case IMPACT:
                return new ResourceLocation("gravisuite", String.format(SHIELD_TEXTURE_HIT, frame));

            case POWER_DOWN:
                return new ResourceLocation("gravisuite", String.format(SHIELD_TEXTURE_POWER_DOWN, frame));

            case POWER_UP:
                return new ResourceLocation("gravisuite", String.format(SHIELD_TEXTURE_POWER_UP, frame));
        }
        return new ResourceLocation("gravisuite", String.format(SHIELD_TEXTURE_HIT, frame));
    }

    @Override
    public void renderParticle(
            Tessellator tessellator,
            float p_70539_2_,
            float p_70539_3_,
            float p_70539_4_,
            float p_70539_5_,
            float p_70539_6_,
            float p_70539_7_) {
        tessellator.draw();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        if (this.model == null) {
            this.model = AdvancedModelLoader.loadModel(MDL_SHIELD);
        }

        float fade = (this.particleAge + p_70539_2_) / this.particleMaxAge;

        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * p_70539_2_ - interpPosX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * p_70539_2_ - interpPosY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * p_70539_2_ - interpPosZ);
        GL11.glTranslated(xx, yy, zz);
        float b = 1.0F;
        int frame = Math.min(15, (int) (14.0F * fade) + 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(getResLoc(frame, this.shieldMode));
        int i = 220;
        int j = i % 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, 0.0f);
        GL11.glRotatef(180.0F - this.rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.rotationPitch, 1.0F, 0.0F, 0.0F);
        GL11.glScaled(0.4D * this.target.height, 0.4D * this.target.height, 0.4D * this.target.height);
        GL11.glColor4f(b, b, b, Math.min(1.0F, (1.0F - fade) * 3.0F));
        this.model.renderAll();

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();

        Minecraft.getMinecraft().renderEngine.bindTexture(EffectRenderer.particleTextures);
        tessellator.startDrawingQuads();
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.posX = this.target.posX;
        this.posY = (this.target.boundingBox.minY + this.target.boundingBox.maxY) / 2.0D;
        this.posZ = this.target.posZ;
    }
}
