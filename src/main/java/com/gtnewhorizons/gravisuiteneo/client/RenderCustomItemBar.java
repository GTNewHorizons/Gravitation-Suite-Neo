package com.gtnewhorizons.gravisuiteneo.client;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.gravisuiteneo.GraviSuiteNeo;
import com.gtnewhorizons.gravisuiteneo.client.ICustomItemBars.BarAlignment;
import com.gtnewhorizons.gravisuiteneo.util.ColorUtil;
import com.gtnewhorizons.gravisuiteneo.util.RenderUtil;
import com.gtnewhorizons.gravisuiteneo.util.vector.Vector4f;

public class RenderCustomItemBar implements IItemRenderer {

    private final RenderItem renderItem = new RenderItem();
    private static final int START_TOP = 2;
    private static final int START_BOTTOM = 14;
    private static final int START_LEFT = 2;
    private static final int START_RIGHT = 14;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (data != null && data.length > 0) {
            this.renderToInventory(item);
        }
    }

    public void renderToInventory(ItemStack itemStack) {
        // First, render the item itself here. In case something goes derp with our bars
        Minecraft mc = Minecraft.getMinecraft();
        this.renderItem.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, 0, 0, true);

        // If the item is registered to use our barrenderer, but doesn't implement the interface, skip here
        if (!(itemStack.getItem() instanceof ICustomItemBars)) {
            return;
        }

        // Grab the basic values
        ICustomItemBars barsItem = (ICustomItemBars) itemStack.getItem();
        int maxBars = barsItem.getNumBars(itemStack); // Number of bars in total
        BarAlignment alignment = barsItem.getBarAlignment(); // The alignment

        // Set the runtime values according to the selected Alignment
        int currentPosXY;
        int thicknessPrefixMultiplicator;
        boolean isVertical = false;

        switch (alignment) {
            case BOTTOM:
                currentPosXY = START_BOTTOM;
                thicknessPrefixMultiplicator = -1;
                break;
            case TOP:
                currentPosXY = START_TOP;
                thicknessPrefixMultiplicator = 1;
                break;
            case LEFT:
                currentPosXY = START_LEFT;
                thicknessPrefixMultiplicator = 1;
                isVertical = true;
                break;
            case RIGHT:
                currentPosXY = START_RIGHT;
                thicknessPrefixMultiplicator = -1;
                isVertical = true;
                break;
            default:
                return;
        }

        for (int i = 0; i < maxBars; i++) {
            // The actual value to display
            double barValue = barsItem.getValueForBar(itemStack, i);
            // The thickness for *this* bar
            int thickness = barsItem.getBarThickness(itemStack, i);

            // If the bar has a value of -1, then it is not drawn; But the space is left "open"
            if (barValue > -1.0D) {
                // Grab the min/max values
                double barMax = barsItem.getMaxValue(itemStack, i);
                Color colorFull = barsItem.getColorForMaxValue(itemStack, i);
                Color colorEmpty = barsItem.getColorForMinValue(itemStack, i);
                boolean tInverted = barsItem.getIsBarInverted(itemStack, i);

                this.renderBar(currentPosXY, barMax, barValue, colorFull, colorEmpty, thickness, isVertical, tInverted);
            }
            // Add the thickness of the current bar to our XY offset.
            // If we go RightToLeft or BottomToTop, we negate the value here; Ending up in a subtract
            // instead of addition
            currentPosXY += thickness * thicknessPrefixMultiplicator;

            // Failsafe to not draw "outside" our boundaries
            if (currentPosXY <= 0 || currentPosXY >= 15) {
                GraviSuiteNeo.LOGGER.warn("[CustomBarItemRenderer] Index violates boundary limits");
                return;
            }
        }
    }

    /**
     * Actually render a bar at position pY
     */
    private void renderBar(int xyStart, double maxDamage, double dispDamage, Color colorFull, Color colorEmpty,
            int thickness, boolean vertical, boolean inverted) {
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            // the percentage of "how damaged" the item is
            double ratio = dispDamage / maxDamage;

            // Get colorVectors as float to calculate with
            Vector4f fgColor = ColorUtil.toFloat(inverted ? colorEmpty : colorFull);
            Vector4f ec = ColorUtil.toFloat(inverted ? colorFull : colorEmpty);

            fgColor.interpolate(ec, (float) ratio);
            Vector4f bgColor = ColorUtil.toFloat(Color.black);
            bgColor.interpolate(fgColor, 0.15f);

            // Get the bar length from the percentage of damage
            int barLenght;
            if (inverted) {
                barLenght = (int) Math.round(12.0 * ratio);
            } else {
                barLenght = (int) Math.round(12.0 * (1 - ratio));
            }

            int tBarX;
            int tBarY;
            int tBarZ;
            int tBgBarWidth;
            int tFgBarWidth;
            int tBgBarHeight;
            int tFgBarHeight;

            if (vertical) { // For Vertical Bars, we need to swap width and height
                tBarX = xyStart;
                tBarY = 2;
                tBarZ = 0;
                tFgBarWidth = thickness;
                tFgBarHeight = barLenght;
                tBgBarWidth = thickness;
                tBgBarHeight = 12;
            } else {
                tBarX = 2;
                tBarY = xyStart;
                tBarZ = 0;
                tFgBarWidth = barLenght;
                tFgBarHeight = thickness;
                tBgBarWidth = 12;
                tBgBarHeight = thickness;
            }

            // Now do the rendering of our bar.
            // All values represent dynamic assigned variables; Whether the bar is Horizontal or Vertical and
            // top<>bottom / right<>left
            RenderUtil.renderQuad2D(tBarX, tBarY, tBarZ, tBgBarWidth, tBgBarHeight, bgColor);
            RenderUtil.renderQuad2D(tBarX, tBarY, tBarZ, tFgBarWidth, tFgBarHeight, fgColor);
        } catch (Exception ignored) {} finally {
            // Failsafe. Make sure we reset GL rendering
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }
}
