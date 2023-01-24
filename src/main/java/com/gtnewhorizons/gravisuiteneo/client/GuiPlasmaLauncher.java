package com.gtnewhorizons.gravisuiteneo.client;

import com.gtnewhorizons.gravisuiteneo.inventory.ContainerPlasmaLauncher;
import gravisuite.ItemSonicLauncher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiPlasmaLauncher extends GuiContainer {

    public static ItemStack getCurrentItem() {
        return Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
    }

    public GuiPlasmaLauncher(ContainerPlasmaLauncher inventorySlots) {
        super(inventorySlots);
        if (!(getCurrentItem().getItem() instanceof ItemSonicLauncher)) {
            return; // Something is wrong; The current item is not a PlasmaLauncher
        }

        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("GraviSuite:textures/gui/PlasmaGunGUI.png"));
        int xStart = (this.width - this.xSize) / 2;
        int yStart = (this.height - this.ySize) / 2;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
