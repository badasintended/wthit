package mcp.mobius.waila.gui.helpers;

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
    
}
