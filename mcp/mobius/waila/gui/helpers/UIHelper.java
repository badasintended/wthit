package mcp.mobius.waila.gui.helpers;

import net.minecraft.client.renderer.Tessellator;

public class UIHelper {
	
    public static void drawTexture(int posX, int posY, int sizeX, int sizeY)
    {
        float zLevel = 0.0F;

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + sizeY), (double)zLevel, 0.0, 1.0);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + sizeY), (double)zLevel, 1.0, 1.0);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + 0),     (double)zLevel, 1.0, 0.0);
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + 0),     (double)zLevel, 0.0, 0.0);
        tess.draw();
    }	
    
}
