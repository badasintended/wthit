package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public class LayoutCropping extends WidgetBase {

	public LayoutCropping(IWidget parent){
		super(parent);
	}

	@Override
	public void draw(){
		this.rez = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);		
		this.saveGLState();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, this.alpha);
		
		this.draw(this.getPos());
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(this.getPos().getX()*this.rez.getScaleFactor(), this.rez.getScaledHeight() - this.getPos().getY()*this.rez.getScaleFactor(), this.getSize().getX()*this.rez.getScaleFactor(), this.getSize().getY()*this.rez.getScaleFactor());
		
		for (IWidget widget: this.widgets.values())
			if (widget.shouldRender())
				widget.draw();		

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		this.loadGLState();		
	}	
	
	@Override
	public void draw(Point pos) {}

}
