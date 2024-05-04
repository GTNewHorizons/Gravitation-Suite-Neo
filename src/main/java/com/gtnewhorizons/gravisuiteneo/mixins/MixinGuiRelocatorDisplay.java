package com.gtnewhorizons.gravisuiteneo.mixins;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.client.SelectedItemMKII;
import com.gtnewhorizons.gravisuiteneo.common.PacketSortingPoints;

import gravisuite.Helpers;
import gravisuite.ItemRelocator;
import gravisuite.ItemRelocator.TeleportPoint;
import gravisuite.ServerProxy;
import gravisuite.client.GuiRelocatorDisplay;
import gravisuite.client.GuiRelocatorDisplay.SelectedItem;
import gravisuite.network.PacketManagePoints;

@Mixin(GuiRelocatorDisplay.class)
public abstract class MixinGuiRelocatorDisplay extends GuiContainer {

    @Unique
    private int pageOffset = 0;

    @Unique
    private int maxOffset = 0;

    @Unique
    private int clickHandled = 0;

    @Unique
    private String mouseDownPointName = null;

    @Unique
    private String sortablePointName = null;

    @Unique
    private static int pageXofYTextX = 23;

    @Unique
    private static int pageXofYTextY = 120;

    @Unique
    private static int prevButtonX = 117;

    @Unique
    private static int prevButtonY = 119;

    @Unique
    private static int nextButtonX = 136;

    @Unique
    private static int nextButtonY = 119;

    @Shadow(remap = false)
    private static ResourceLocation tex;

    @Shadow(remap = false)
    private int mouseX;

    @Shadow(remap = false)
    private int mouseY;

    @Shadow(remap = false)
    private int itemInterval;

    @Shadow(remap = false)
    private int firstItemX;

    @Shadow(remap = false)
    private int firstItemY;

    @Shadow(remap = false)
    private int cancelBtnX1;

    @Shadow(remap = false)
    private int firstSelX;

    @Shadow(remap = false)
    private int firstSelY;

    @Shadow(remap = false)
    private int selWidth;

    @Shadow(remap = false)
    private int firstItemBGX;

    @Shadow(remap = false)
    private int firstItemBGY;

    @Shadow(remap = false)
    private int itemBGinterval;

    @Shadow(remap = false)
    private int cancelBtnWidth;

    @Shadow(remap = false)
    private int openType;

    @Shadow(remap = false)
    private int itemBGX;

    @Shadow(remap = false)
    private int itemBGY;

    @Shadow(remap = false)
    private int itemBGWidth;

    @Shadow(remap = false)
    private int itemBGHeight;

    @Shadow(remap = false)
    private int itemBGdefX;

    @Shadow(remap = false)
    private int itemBGdefY;

    @Shadow(remap = false)
    private int itemBGselX;

    @Shadow(remap = false)
    private int itemBGselY;

    @Shadow(remap = false)
    private int itemBGdelX;

    @Shadow(remap = false)
    private int itemBGdelY;

    static {
        tex = new ResourceLocation(GraviSuiteNeo.MODID, "textures/gui/relocator_display2.png");
    }

    /**
     * Get the current selected index from the Dislocator; According to the mouse position
     *
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite(remap = false)
    public SelectedItem getSelected(List<TeleportPoint> tpList) {
        return null;
    }

    public SelectedItemMKII getSelectedMKII(List<TeleportPoint> tpList) {
        if (tpList == null || tpList.isEmpty()) {
            return null;
        }

        SelectedItemMKII tmpSel = new SelectedItemMKII();
        int itemsCount = tpList.size();

        if (this.func_146978_c(
                this.firstSelX,
                this.firstSelY + 1,
                this.selWidth,
                Math.min(this.itemInterval * 10 - 2, this.itemInterval * itemsCount - 2),
                this.mouseX,
                this.mouseY)) {

            int yStart = (this.height - this.ySize) / 2;
            int realMouseY = this.mouseY - yStart;
            double newCalc = (double) (realMouseY - this.firstSelY + 1) / (double) this.itemBGinterval;
            tmpSel.guiIDX = (int) Math.ceil(newCalc);
            tmpSel.offset = this.pageOffset;

            if (this.func_146978_c(
                    this.cancelBtnX1,
                    this.firstSelY + 1,
                    this.cancelBtnWidth,
                    Math.min(this.itemInterval * 10 - 1, this.itemInterval * itemsCount - 1),
                    this.mouseX,
                    this.mouseY)) {

                tmpSel.delFlag = true;
            }

            if (tpList.size() > tmpSel.getRealIDX()) {
                return tmpSel;
            }
        }

        return null;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int shift = Mouse.getEventDWheel();

        if (shift != 0) {
            int mousex = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mousey = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            mouseScrolled(mousex, mousey, shift > 0 ? 1 : -1);
        }
    }

    @Override
    public void mouseMovedOrUp(int mousex, int mousey, int button) {
        if (button >= 0 && (clickHandled & 1 << button) != 0) {
            clickHandled &= ~(1 << button);
            mouseUp(mousex, mousey, button);
        } else {
            super.mouseMovedOrUp(mousex, mousey, button);
        }
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite
    public void mouseClicked(int mousex, int mousey, int button) {
        super.mouseClicked(mousex, mousey, button);
        onMouseDown(mousex, mousey, button);
        clickHandled |= 1 << button;
    }

    protected void onMouseDown(int mousex, int mousey, int button) {
        EntityPlayer player = this.mc.thePlayer;
        List<TeleportPoint> tpList = ItemRelocator.loadTeleportPoints(player.inventory.getCurrentItem());
        SelectedItemMKII selectedItem = this.getSelectedMKII(tpList);

        if (selectedItem != null) {
            this.mouseDownPointName = tpList.get(selectedItem.getRealIDX()).pointName;
        }
    }

    public void mouseUp(int mousex, int mousey, int button) {
        this.mouseDownPointName = null;

        if (this.sortablePointName != null) {
            this.sortablePointName = null;
        } else if (this.func_146978_c(prevButtonX, prevButtonY, 9, 9, mousex, mousey)) { // Previous-Button
            if (this.pageOffset > 0) {
                this.pageOffset--;
            }
        } else if (this.func_146978_c(nextButtonX, nextButtonY, 9, 9, mousex, mousey)) { // Next-Button
            if (this.pageOffset < this.maxOffset) {
                this.pageOffset++;
            }
        } else {

            EntityPlayer player = this.mc.thePlayer;
            // Load TP-Destinations from our ItemStack
            List<TeleportPoint> tpList = ItemRelocator.loadTeleportPoints(player.inventory.getCurrentItem());
            if (tpList == null) {
                return;
            }

            // Get the current selected Item that has been clicked; If any
            SelectedItemMKII selectedItem = this.getSelectedMKII(tpList);
            if (selectedItem == null) {
                return;
            }

            int realIDX = selectedItem.getRealIDX();

            if (realIDX >= tpList.size()) {
                GraviSuiteNeo.LOGGER.error("WARNING: Caught IndexOutOfBounds while using Translocator");
                ServerProxy.sendPlayerMessage(
                        player,
                        EnumChatFormatting.RED + StatCollector.translateToLocal("message.relocator.text.invalid"));
                player.closeScreen();
                return;
            }

            String tPointName = tpList.get(selectedItem.getRealIDX()).pointName;

            if (selectedItem.delFlag) {
                ItemRelocator.removeTeleportPoint(player.getCurrentEquippedItem(), tPointName);
                PacketManagePoints.issue(player, tPointName, (byte) 0);
            } else {

                if (this.openType == 0) {
                    PacketManagePoints.issue(player, tPointName, (byte) 2);
                }
                if (this.openType == 1) {
                    PacketManagePoints.issue(player, tPointName, (byte) 3);
                }

                player.closeScreen();
            }
        }

    }

    @Override
    public void mouseClickMove(int mousex, int mousey, int button, long heldTime) {
        EntityPlayer player = this.mc.thePlayer;
        List<TeleportPoint> tpList = ItemRelocator.loadTeleportPoints(player.inventory.getCurrentItem());
        SelectedItemMKII selectedItem = this.getSelectedMKII(tpList);
        int realIDX = selectedItem != null ? selectedItem.getRealIDX() : -1;

        if (button != 0 || tpList == null || realIDX == -1 || this.mouseDownPointName == null) {
            super.mouseClickMove(mousex, mousey, button, heldTime);
            return;
        }

        TeleportPoint point = tpList.get(realIDX);

        if (this.sortablePointName == null
                && (!point.pointName.equalsIgnoreCase(this.mouseDownPointName) || heldTime > 250)) {
            this.sortablePointName = this.mouseDownPointName;
        } else if (this.sortablePointName != null && !point.pointName.equalsIgnoreCase(this.sortablePointName)) {
            PacketSortingPoints.movePoint(player, (byte) realIDX, this.sortablePointName);
            PacketSortingPoints.issue(player, (byte) realIDX, this.sortablePointName);
        } else {
            super.mouseClickMove(mousex, mousey, button, heldTime);
        }
    }

    public void mouseScrolled(int mousex, int mousey, int shift) {

        if (this.func_146978_c(
                this.firstSelX,
                this.firstSelY + 1,
                this.selWidth,
                this.itemInterval * 10 - 2,
                mousex,
                mousey)) {
            if (shift > 0 && this.pageOffset > 0) {
                this.pageOffset--;
            } else if (shift < 0 && this.pageOffset < this.maxOffset) {
                this.pageOffset++;
            }
        }

    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        int xStart = (this.width - this.xSize) / 2;
        int yStart = (this.height - this.ySize) / 2;
        try {
            List<TeleportPoint> tpList = ItemRelocator.loadTeleportPoints(this.mc.thePlayer.inventory.getCurrentItem());
            this.maxOffset = 0;

            if (tpList != null && !tpList.isEmpty()) {
                this.maxOffset = (int) Math.floor((tpList.size() - 1) / 10);
                SelectedItemMKII selectedItem = this.getSelectedMKII(tpList);
                int realIDX = selectedItem != null ? selectedItem.getRealIDX() : -2;

                for (int index = this.pageOffset * 10; index < (this.pageOffset + 1) * 10
                        && index < tpList.size(); ++index) {
                    TeleportPoint tmpPoint = tpList.get(index);
                    int state = 0;

                    if (realIDX == index) {
                        state = selectedItem.delFlag ? 2 : 1;
                    }

                    drawItem(tmpPoint, index, state);
                }

                if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindSneak) && selectedItem != null) {
                    List<String> toolTipData = new ArrayList<>();
                    TeleportPoint tmpPoint = tpList.get(realIDX);

                    toolTipData.add(
                            StatCollector.translateToLocal("message.relocator.text.dim") + ": "
                                    + DimensionManager.getProvider(tmpPoint.dimID).getDimensionName());
                    toolTipData.add("X: " + (int) tmpPoint.x);
                    toolTipData.add("Y: " + (int) tmpPoint.y);
                    toolTipData.add("Z: " + (int) tmpPoint.z);
                    int realMouseX = this.mouseX - xStart;
                    int realMouseY = this.mouseY - yStart;
                    Helpers.renderTooltip(realMouseX - 2, realMouseY, toolTipData);
                }
            }
        } catch (Exception e) {
            GraviSuiteNeo.LOGGER.error("GuiRelocatorDisplay#drawGuiContainerForegroundLayer failed!", e);
        }
    }

    /**
     * @author Namikon, glowredman
     * @reason Gravitation Suite Neo
     */
    @Overwrite
    protected void drawGuiContainerBackgroundLayer(float opacity, int x, int y) {
        try {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(tex);

            int xStart = (this.width - this.xSize) / 2;
            int yStart = (this.height - this.ySize) / 2;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(xStart, yStart, 0, 0, this.xSize, this.ySize);
            GL11.glDisable(GL11.GL_BLEND);

            if (this.prevButtonAvailable()) {
                this.drawTexturedModalRect(xStart + prevButtonX, yStart + prevButtonY, 0, 182, 9, 9);
            } else {
                this.drawTexturedModalRect(xStart + prevButtonX, yStart + prevButtonY, 0, 192, 9, 9);
            }

            if (this.nextButtonAvailable()) {
                this.drawTexturedModalRect(xStart + nextButtonX, yStart + nextButtonY, 17, 182, 9, 9);
            } else {
                this.drawTexturedModalRect(xStart + nextButtonX, yStart + nextButtonY, 17, 192, 9, 9);
            }

            // Draw "x / y" Page-indicator
            this.fontRendererObj.drawString(
                    String.format("%d / %d", this.pageOffset + 1, this.maxOffset + 1),
                    xStart + pageXofYTextX,
                    yStart + pageXofYTextY,
                    0xFFFFFF);
        } catch (Exception e) {
            GraviSuiteNeo.LOGGER.error("GuiRelocatorDisplay#drawGuiContainerBackgroundLayer failed!", e);
        }
    }

    private void drawItem(TeleportPoint tmpPoint, int index, int state) {
        this.mc.getTextureManager().bindTexture(tex);

        if (state == 1) {
            this.drawTexturedModalRect(
                    this.firstItemBGX,
                    this.firstItemBGY + (index - this.pageOffset * 10) * this.itemInterval,
                    this.itemBGselX,
                    this.itemBGselY,
                    this.itemBGWidth,
                    this.itemBGHeight);
        } else if (state == 2) {
            this.drawTexturedModalRect(
                    this.firstItemBGX,
                    this.firstItemBGY + (index - this.pageOffset * 10) * this.itemInterval,
                    this.itemBGdelX,
                    this.itemBGdelY,
                    this.itemBGWidth,
                    this.itemBGHeight);
        } else if (tmpPoint.defPoint) {
            this.drawTexturedModalRect(
                    this.firstItemBGX,
                    this.firstItemBGY + (index - this.pageOffset * 10) * this.itemInterval,
                    this.itemBGdefX,
                    this.itemBGdefY,
                    this.itemBGWidth,
                    this.itemBGHeight);
        } else {
            this.drawTexturedModalRect(
                    this.firstItemBGX,
                    this.firstItemBGY + (index - this.pageOffset * 10) * this.itemInterval,
                    this.itemBGX,
                    this.itemBGY,
                    this.itemBGWidth,
                    this.itemBGHeight);
        }

        this.fontRendererObj.drawString(
                tmpPoint.pointName,
                this.firstItemX,
                this.firstItemY + (index - this.pageOffset * 10) * this.itemInterval,
                0xFFFFFF);
    }

    @Unique
    private boolean prevButtonAvailable() {
        return this.pageOffset > 0;
    }

    @Unique
    private boolean nextButtonAvailable() {
        return this.pageOffset < this.maxOffset;
    }

    private MixinGuiRelocatorDisplay(Container p_i1072_1_) {
        super(p_i1072_1_);
    }
}
