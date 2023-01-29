package com.gtnewhorizons.gravisuiteneo.client;

import java.awt.Point;

public class GuiRegion {

    public int Top;
    public int Left;
    public int Width;
    public int Height;
    public int ParentGuiLeft;
    public int ParentGuiTop;

    public GuiRegion(int parentGuiLeft, int parentGuiTop) {
        this(parentGuiLeft, parentGuiTop, 0, 0, 0, 0);
    }

    public GuiRegion(int pParentGuiLeft, int pParentGuiTop, int pTop, int pLeft, int pWidth, int pHeight) {
        this.ParentGuiLeft = pParentGuiLeft;
        this.ParentGuiTop = pParentGuiTop;
        this.Top = pTop;
        this.Left = pLeft;
        this.Width = pWidth;
        this.Height = pHeight;
    }

    /**
     * Check if given point is within the GUI Region. This method is basically a custom implementation of MC's
     * GuiContainer-class function func_146978_c. But as it is protected... Yeah...
     */
    public boolean isPointInRegion(Point point) {
        int x = point.x - this.ParentGuiLeft;
        int y = point.y - this.ParentGuiTop;
        return x >= this.Left - 1 && x < this.Left + this.Width && y >= this.Top - 1 && y < this.Top + this.Height + 1;
    }
}
