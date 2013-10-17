package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.util.Point;

public abstract class ButtonBase extends WidgetBase {

	protected boolean mouseOver = false;
	protected static ResourceLocation widgetsTexture = new ResourceLocation("textures/gui/widgets.png");
	
	public ButtonBase(IWidget parent){
		super(parent);
	}
	
	@Override
	public void draw(Point pos) {
		this.saveGLState();
		int texOffset = 0;
		if (this.mouseOver)
			texOffset = 1;
		
		this.mc.func_110434_K().func_110577_a(widgetsTexture);
		UIHelper.drawTexture(this.getPos().getX(), this.getPos().getY(), this.getSize().getX(), this.getSize().getY(), 0, 66 + texOffset*20, 200, 20);
		
		this.loadGLState();
	}

	@Override
	public void onMouseEnter(MouseEvent event){
		mouseOver = true;
	}

	@Override
	public void onMouseLeave(MouseEvent event){
		mouseOver = false;
	}	

}
