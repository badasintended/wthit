package mcp.mobius.waila.gui.widgets.buttons;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;

public class ButtonBooleanConfigRemote extends ButtonBooleanConfig {

	public ButtonBooleanConfigRemote(IWidget parent, String configKey, String textFalse, String textTrue){
		this(parent, configKey, true, true, textFalse, textTrue);
	}	
	
	public ButtonBooleanConfigRemote(IWidget parent, String configKey, boolean instant, boolean state_, String textFalse, String textTrue){
		super(parent, configKey, instant, state_, textFalse, textTrue);
		if (!Waila.instance.serverPresent)
			this.state = false;
		
		if (this.state){
			this.getWidget("LabelTrue").show();
			this.getWidget("LabelFalse").hide();
		} else {
			this.getWidget("LabelTrue").hide();
			this.getWidget("LabelFalse").show();			
		}		
	}

	@Override
	public void onMouseClick(MouseEvent event){
		if (Waila.instance.serverPresent)
			super.onMouseClick(event);
	}	
	
	@Override
	public void draw(Point pos) {
		if (Waila.instance.serverPresent)
			super.draw(pos);		
		else{
			this.saveGLState();
			int  texOffset = -1;
			this.mc.getTextureManager().bindTexture(widgetsTexture);
			UIHelper.drawTexture(this.getPos().getX(), this.getPos().getY(), this.getSize().getX(), this.getSize().getY(), 0, 66 + texOffset*20, 200, 20);
			this.loadGLState();
		}
	}	
}
