package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public class PictureDisplay extends WidgetBase {

	private ResourceLocation texture;
	
	public PictureDisplay(IWidget parent, String uri){
		super(parent);
		this.texture = new ResourceLocation(uri);
	}
	
	@Override
	public void draw(Point pos) {
		this.saveGLState();
		
		GL11.glPushMatrix();
		this.texManager.func_110577_a(texture);
		this.drawTexture(pos.getX(), pos.getY());
		GL11.glPopMatrix();

		this.loadGLState();
	}

    private void drawTexture(int posX, int posY)
    {
        float zLevel = 0.0F;
        int sizeX = this.getSize().getX();
        int sizeY = this.getSize().getY();
        
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + sizeY), (double)zLevel, 0.0, 1.0);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + sizeY), (double)zLevel, 1.0, 1.0);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + 0),     (double)zLevel, 1.0, 0.0);
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + 0),     (double)zLevel, 0.0, 0.0);
        tess.draw();
    }	
	
}
