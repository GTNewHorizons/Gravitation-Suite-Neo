package com.gtnewhorizons.gravisuiteneo.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import com.gtnewhorizons.gravisuiteneo.client.FXQuantumShield;
import com.gtnewhorizons.gravisuiteneo.client.RenderCustomItemBar;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import gravisuite.GraviSuite;
import gravisuite.network.PacketKeyPress;

public class ClientProxy extends CommonProxy {

    private static final double RAD_TO_DEG = 180.0D / Math.PI;
    private static final KeyBinding MEDICAL_KEY = new KeyBinding("GraviSuite MedKit", Keyboard.KEY_NONE, "GraviSuite");

    @Override
    public void registerRenderers() {
        RenderCustomItemBar cbir = new RenderCustomItemBar();
        MinecraftForgeClient.registerItemRenderer(GraviSuiteNeoRegistry.itemPlasmaCell, cbir);
        MinecraftForgeClient.registerItemRenderer(GraviSuite.sonicLauncher, cbir);
    }

    @Override
    public void registerKeys() {
        ClientRegistry.registerKeyBinding(MEDICAL_KEY);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void createQuantumShieldFX(int source, int target) {
        Minecraft mc = FMLClientHandler.instance().getClient();

        Entity sourceEntity = mc.theWorld.getEntityByID(source);
        if (sourceEntity == null) {
            return;
        }

        switch (target) {
            case -1 -> {
                FXQuantumShield qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        90.0F,
                        FXQuantumShield.EShieldMode.IMPACT);
                mc.effectRenderer.addEffect(qs);
                qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        270.0F,
                        FXQuantumShield.EShieldMode.IMPACT);
                mc.effectRenderer.addEffect(qs);
            }

            case -2 -> {
                FXQuantumShield qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        270.0F,
                        FXQuantumShield.EShieldMode.IMPACT);

                mc.effectRenderer.addEffect(qs);
            }

            case -3 -> {
                FXQuantumShield qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        90.0F,
                        FXQuantumShield.EShieldMode.IMPACT);

                mc.effectRenderer.addEffect(qs);
            }

            case -4 -> {
                FXQuantumShield qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        90.0F,
                        FXQuantumShield.EShieldMode.POWER_UP);
                mc.effectRenderer.addEffect(qs);
                qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        270.0F,
                        FXQuantumShield.EShieldMode.POWER_UP);
                mc.effectRenderer.addEffect(qs);
            }

            case -5 -> {
                FXQuantumShield qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        90.0F,
                        FXQuantumShield.EShieldMode.POWER_DOWN);
                mc.effectRenderer.addEffect(qs);
                qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        0.0F,
                        270.0F,
                        FXQuantumShield.EShieldMode.POWER_DOWN);
                mc.effectRenderer.addEffect(qs);
            }
            default -> {
                if (target < 0) return;

                Entity targetEntity = mc.theWorld.getEntityByID(target);
                float pitch;
                float yaw;

                if (targetEntity != null) {
                    double dX = sourceEntity.posX - targetEntity.posX;
                    double dY = (sourceEntity.boundingBox.minY + sourceEntity.boundingBox.maxY) / 2.0D
                            - (targetEntity.boundingBox.minY + targetEntity.boundingBox.maxY) / 2.0D;
                    double dZ = sourceEntity.posZ - targetEntity.posZ;
                    double dXZ = Math.sqrt(dX * dX + dZ * dZ);
                    yaw = (float) (Math.atan2(dZ, dX) * RAD_TO_DEG) - 90.0F;
                    pitch = (float) -(Math.atan2(dY, dXZ) * RAD_TO_DEG);
                } else {
                    pitch = 90.0F;
                    yaw = 0.0F;
                }

                FXQuantumShield qs = new FXQuantumShield(
                        mc.theWorld,
                        sourceEntity.posX,
                        sourceEntity.posY,
                        sourceEntity.posZ,
                        sourceEntity,
                        8,
                        yaw,
                        pitch,
                        FXQuantumShield.EShieldMode.IMPACT);

                mc.effectRenderer.addEffect(qs);
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (!FMLClientHandler.instance().isGUIOpen(GuiChat.class) && MEDICAL_KEY.isPressed()) {
            ItemStack itemstack = Minecraft.getMinecraft().thePlayer.inventory.armorItemInSlot(2);
            if (itemstack != null && itemstack.getItem() == GraviSuite.graviChestPlate) {
                PacketKeyPress.issue(4);
            }
        }
    }
}
