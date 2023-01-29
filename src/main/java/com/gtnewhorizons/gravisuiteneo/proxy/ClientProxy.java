package com.gtnewhorizons.gravisuiteneo.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeoRegistry;
import com.gtnewhorizons.gravisuiteneo.client.RenderCustomItemBar;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import gravisuite.GraviSuite;
import gravisuite.network.PacketKeyPress;

public class ClientProxy extends CommonProxy {

    public static final KeyBinding MEDICAL_KEY = new KeyBinding("GraviSuite MedKit", Keyboard.KEY_NONE, "GraviSuite");

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
