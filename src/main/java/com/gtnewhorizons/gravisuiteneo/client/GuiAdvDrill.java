package com.gtnewhorizons.gravisuiteneo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.util.LevelableToolHelper;
import gravisuite.ItemAdvDDrill;

public class GuiAdvDrill extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE_T2 = new ResourceLocation(
            GraviSuiteNeo.MODID,
            "textures/gui/DrillGUITier2.png");
    private static final ResourceLocation GUI_TEXTURE_T3 = new ResourceLocation(
            GraviSuiteNeo.MODID,
            "textures/gui/DrillGUITier3.png");

    private int tier;
    private ItemStack currentDrill;

    public static ItemStack getCurrentItem() {
        return Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
    }

    public GuiAdvDrill(InventoryPlayer inventorySlots) {
        // super(new ContainerAdvDrill(inventorySlots, getCurrentItem()));
        super(null);
        this.currentDrill = getCurrentItem();
        if (!(getCurrentItem().getItem() instanceof ItemAdvDDrill)) {
            return; // Something is wrong; The current item is not an Adv Drill
        }

        this.tier = LevelableToolHelper.getLevel(this.currentDrill);
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.tier == 1) {
            this.mc.renderEngine.bindTexture(GUI_TEXTURE_T2);
        } else if (this.tier == 2) {
            this.mc.renderEngine.bindTexture(GUI_TEXTURE_T3);
        }
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
