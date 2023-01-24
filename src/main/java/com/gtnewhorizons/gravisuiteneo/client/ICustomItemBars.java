package com.gtnewhorizons.gravisuiteneo.client;

import java.awt.Color;
import net.minecraft.item.ItemStack;

/**
 * An Interface to give your Item one ore more fancy looking status-bars.
 * You need to disable the vanilla durability bar! Make sure to Override showDurabilityBar() and return false
 */
public interface ICustomItemBars {

    enum BarAlignment {
        /**
         * The Bars grow from top to bottom, where 0 is the topmost
         */
        TOP,

        /**
         * The Bars grow from bottom to the top, where 0 is the topmost
         */
        BOTTOM,

        /**
         * The Bars grow from left to right, where 0 is the leftmost
         */
        LEFT,

        /**
         * The Bars grow from right to left, where 0 is the leftmost
         */
        RIGHT
    }

    /**
     * @return The Number of Bars that you want to have rendered for your Item in the players Inventory
     */
    int getNumBars(ItemStack itemStack);

    /**
     * @return The java.awt.Color representation for the Color of your bar if it heads towards Zero
     */
    Color getColorForMinValue(ItemStack itemStack, int barIndex);

    /**
     * @return The java.awt.Color representation for the Color of your bar if it heads towards the Maximum
     */
    Color getColorForMaxValue(ItemStack itemStack, int barIndex);

    /**
     * @return The maximum value for bar Nr. barIndex
     */
    double getMaxValue(ItemStack itemStack, int barIndex);

    /**
     * @return The Value for the bar to be displayed. You can return -1 here to disable the bar (No black background bar will be drawn for this bar index)
     */
    double getValueForBar(ItemStack itemStack, int barIndex);

    /**
     * @return The bar alignment overlay
     */
    BarAlignment getBarAlignment();

    /**
     * @return The thickness (in pixels) you want bar ID x to be
     */
    int getBarThickness(ItemStack itemStack, int barIndex);

    /**
     * @return False if you want to display a durability bar like vanilla, True if you want to go 0% Empty - 100% Full
     */
    boolean getIsBarInverted(ItemStack itemStack, int barIndex);
}
