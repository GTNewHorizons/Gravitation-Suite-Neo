package com.gtnewhorizons.gravisuiteneo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.inventory.ContainerPlasmaLauncher;
import com.gtnewhorizons.gravisuiteneo.items.ItemPlasmaLauncher;

public class GuiPlasmaLauncher extends GuiContainer {

    private static final ResourceLocation TEXTURE_GUI = new ResourceLocation(
            GraviSuiteNeo.MODID,
            "textures/gui/PlasmaGunGUI.png");

    public static ItemStack getCurrentItem() {
        return Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
    }

    public GuiPlasmaLauncher(ContainerPlasmaLauncher inventorySlots) {
        super(inventorySlots);
        if (!(getCurrentItem().getItem() instanceof ItemPlasmaLauncher)) {
            return; // Something is wrong; The current item is not a PlasmaLauncher
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEXTURE_GUI);
        int xStart = (this.width - this.xSize) / 2;
        int yStart = (this.height - this.ySize) / 2;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
