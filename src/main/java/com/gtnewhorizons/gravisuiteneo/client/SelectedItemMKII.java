package com.gtnewhorizons.gravisuiteneo.client;

import gravisuite.client.GuiRelocatorDisplay.SelectedItem;

public class SelectedItemMKII extends SelectedItem {

    public int guiIDX = -1;
    public int offset = 0;

    public int getRealIDX() {
        return this.guiIDX + this.offset * 10 - 1;
    }
}
