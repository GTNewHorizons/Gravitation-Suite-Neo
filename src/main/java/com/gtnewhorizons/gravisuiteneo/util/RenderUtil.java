package com.gtnewhorizons.gravisuiteneo.util;

import com.gtnewhorizons.gravisuiteneo.util.vector.Vector4f;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class RenderUtil {

    public static void renderQuad2D(double x, double y, double z, double width, double height, int colorRGB) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(colorRGB);
        tessellator.addVertex(x, y + height, z);
        tessellator.addVertex(x + width, y + height, z);
        tessellator.addVertex(x + width, y, z);
        tessellator.addVertex(x, y, z);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void renderQuad2D(double x, double y, double z, double width, double height, Vector4f colorRGBA) {
        GL11.glColor4f(colorRGBA.x, colorRGBA.y, colorRGBA.z, colorRGBA.w);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(x, y + height, z);
        tessellator.addVertex(x + width, y + height, z);
        tessellator.addVertex(x + width, y, z);
        tessellator.addVertex(x, y, z);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
