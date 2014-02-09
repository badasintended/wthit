package mcp.mobius.waila.gui.widgets.buttons;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;

public class ButtonBooleanConfig extends ButtonBoolean {
	
	private String configKey;
	private boolean instant;

	public ButtonBooleanConfig(IWidget parent, String configKey, String textFalse, String textTrue){
		this(parent, configKey, true, true, textFalse, textTrue);
	}
	
	public ButtonBooleanConfig(IWidget parent, String configKey, boolean instant, boolean state_, String textFalse, String textTrue){
		super(parent, textFalse, textTrue);
		this.configKey = configKey;
		this.instant   = instant;
		
		Waila.instance.config.load();
		Property prop = Waila.instance.config.get(Configuration.CATEGORY_GENERAL, this.configKey, state_);
		this.state = prop.getBoolean(state_);	
		
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
		super.onMouseClick(event);
		
		if (this.instant){
			Waila.instance.config.getCategory(Configuration.CATEGORY_GENERAL).put(this.configKey, new Property(this.configKey,String.valueOf(this.state),Property.Type.BOOLEAN));
			Waila.instance.config.save();
		}
	}
	
}
