package mcp.mobius.waila.gui.helpers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

public class UIHelper {
	
    public static void drawTexture(int posX, int posY, int sizeX, int sizeY)
    {
    	UIHelper.drawTexture(posX, posY, sizeX, sizeY, 0, 0, 256, 256);
    }	
    
    public static void drawTexture(int posX, int posY, int sizeX, int sizeY, int texU, int texV, int texSizeU, int texSizeV)
    {
        float zLevel = 0.0F;
        float f = 0.00390625F;
        
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + sizeY), (double)zLevel, texU*f, (texV + texSizeV)*f);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + sizeY), (double)zLevel, (texU + texSizeU)*f, (texV + texSizeV)*f);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + 0),     (double)zLevel, (texU + texSizeU)*f, texV*f);
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + 0),     (double)zLevel, texU*f, texV*f);
        tess.draw();
    }	    
    
    public static void drawGradientRect(int minx, int miny, int maxx, int maxy, int zlevel, int color1, int color2)
    {
        float alpha1 = (float)(color1 >> 24 & 255) / 255.0F;
        float red1   = (float)(color1 >> 16 & 255) / 255.0F;
        float green1 = (float)(color1 >> 8 & 255) / 255.0F;
        float blue1  = (float)(color1 & 255) / 255.0F;
        float alpha2 = (float)(color2 >> 24 & 255) / 255.0F;
        float red2   = (float)(color2 >> 16 & 255) / 255.0F;
        float green2 = (float)(color2 >> 8 & 255) / 255.0F;
        float blue2  = (float)(color2 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(red1, green1, blue1, alpha1);
        tessellator.addVertex((double)maxx, (double)miny, (double)zlevel);
        tessellator.addVertex((double)minx, (double)miny, (double)zlevel);
        tessellator.setColorRGBA_F(red2, green2, blue2, alpha2);
        tessellator.addVertex((double)minx, (double)maxy, (double)zlevel);
        tessellator.addVertex((double)maxx, (double)maxy, (double)zlevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }    
    
}
