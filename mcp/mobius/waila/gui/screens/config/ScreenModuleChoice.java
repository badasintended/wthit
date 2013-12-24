package mcp.mobius.waila.gui.screens.config;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainer;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import mcp.mobius.waila.gui_old.widget_old.ButtonChangeScreen;
import net.minecraft.client.gui.GuiScreen;

public class ScreenModuleChoice extends ScreenBase {
	
	public ScreenModuleChoice(GuiScreen parent){
		super(parent);

		this.getRoot().addWidget("ButtonContainer", new ButtonContainer(this.getRoot(), 3, 100, 25.0));
		this.getRoot().getWidget("ButtonContainer").setGeometry(new WidgetGeometry(50.0, 20.0, 400.0, 60.0, CType.RELXY, CType.REL_Y, WAlign.CENTER, WAlign.TOP));
		
		ButtonContainer buttonContainer = ((ButtonContainer)this.getRoot().getWidget("ButtonContainer"));
		
        for (String key : ConfigHandler.instance().getModuleNames())
        	buttonContainer.addButton( new ButtonScreenChange(this.getRoot(), key, new ScreenModuleConfig(this, key)));
		
		this.getRoot().addWidget("LayoutBack", new LayoutBase(this.getRoot()));
		this.getRoot().getWidget("LayoutBack").setGeometry(new WidgetGeometry(0.0, 80.0, 100.0, 20.0, CType.RELXY, CType.RELXY));
		this.getRoot().getWidget("LayoutBack").addWidget("ButtonBack", new ButtonScreenChange(this.getRoot().getWidget("LayoutBack"), "Back", this.parent));
		this.getRoot().getWidget("LayoutBack").getWidget("ButtonBack").setGeometry(new WidgetGeometry(50.0, 50.0, 100.0, 20.0, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
	}
}
