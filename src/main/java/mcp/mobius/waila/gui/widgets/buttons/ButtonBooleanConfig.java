package mcp.mobius.waila.gui.widgets.buttons;

import org.lwjgl.util.Point;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;

public class ButtonBooleanConfig extends ButtonBoolean {
	
	protected String category;
	protected String configKey;
	protected boolean instant;

	public ButtonBooleanConfig(IWidget parent, String category, String configKey, String textFalse, String textTrue){
		this(parent, category, configKey, true, true, textFalse, textTrue);
	}
	
	public ButtonBooleanConfig(IWidget parent, String category, String configKey, boolean instant, boolean state_, String textFalse, String textTrue){
		super(parent, textFalse, textTrue);
		this.category  = category;
		this.configKey = configKey;
		this.instant   = instant;
		
		this.state = ConfigHandler.instance().getConfig(this.category, this.configKey, state_);
		
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
		if (!ConfigHandler.instance().forcedConfigs.containsKey(this.configKey))		
			super.onMouseClick(event);
		
		if (this.instant)
			ConfigHandler.instance().setConfig(this.category, this.configKey, this.state);
	}
	
	@Override
	public void draw(Point pos) {
		if (!ConfigHandler.instance().forcedConfigs.containsKey(this.configKey))
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
