package mcp.mobius.waila.gui.widgets.buttons;

import net.minecraftforge.common.config.Configuration;

import org.lwjgl.util.Point;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.WidgetBase;

public class ButtonIntegerConfig extends ButtonInteger {

	private String category;
	private String configKey;
	private boolean instant;

	public ButtonIntegerConfig(IWidget parent, String category,  String configKey, String... texts){
		this(parent, category, configKey, true, 0, texts);
	}
	
	public ButtonIntegerConfig(IWidget parent, String category, String configKey, boolean instant, int state_, String... texts){
		super(parent, texts);
		this.category  = category;
		this.configKey = configKey;
		this.instant   = instant;
		
		this.state = ConfigHandler.instance().getConfig(this.category, this.configKey, state_);
		
		for (int i = 0; i < this.nStates; i++)
			this.getWidget(String.format("Label_%d", i)).hide();
		
		this.getWidget(String.format("Label_%d", state)).show();			
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		super.onMouseClick(event);
		
		if (this.instant)
			ConfigHandler.instance().setConfig(this.category, this.configKey, this.state);
	}

}
