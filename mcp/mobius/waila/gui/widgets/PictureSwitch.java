package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public class PictureSwitch extends WidgetBase {

	private ResourceLocation texture1;
	private ResourceLocation texture2;
	private boolean mouseOver = false;
	
	public PictureSwitch(IWidget parent, String uri1, String uri2){
		super(parent);
		this.texture1 = new ResourceLocation(uri1);
		this.texture2 = new ResourceLocation(uri2);
	}
	
	@Override
	public void draw(Point pos) {
		this.saveGLState();
		
		ResourceLocation texture = mouseOver ? this.texture2 : this.texture1;
		
		GL11.glPushMatrix();
		this.texManager.func_110577_a(texture);
		UIHelper.drawTexture(pos.getX(), pos.getY(), this.getSize().getX(), this.getSize().getY());
		GL11.glPopMatrix();

		this.loadGLState();
	}
	
	@Override
	public void onMouseEnter(MouseEvent event){
		//System.out.printf("%s %s\n", this, event);
		mouseOver = true;
	}

	@Override
	public void onMouseLeave(MouseEvent event){
		//System.out.printf("%s %s\n", this, event);
		mouseOver = false;
	}	
	
}
