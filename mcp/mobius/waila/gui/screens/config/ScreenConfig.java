package mcp.mobius.waila.gui.screens.config;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonBooleanConfig;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainer;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainerLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import net.minecraft.client.gui.GuiScreen;

public class ScreenConfig extends ScreenBase {
	public ScreenConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("ButtonContainer", new ButtonContainer(this.getRoot(), 2, 100, 25.0));
		this.getRoot().getWidget("ButtonContainer").setGeometry(new WidgetGeometry(50.0, 50.0, 50.0, 30.0, CType.RELXY, CType.REL_X, WAlign.CENTER, WAlign.CENTER));	
		
		ButtonContainer buttonContainer = ((ButtonContainer)this.getRoot().getWidget("ButtonContainer"));
		
		buttonContainer.addButton( new ButtonScreenChange(this.getRoot(), "screen.button.waila",   new ScreenWailaConfig(this)));
		buttonContainer.addButton( new ButtonScreenChange(this.getRoot(), "screen.button.modules", new ScreenModuleChoice(this)));
		
		this.getRoot().addWidget("LayoutBack", new LayoutBase(this.getRoot()));
		this.getRoot().getWidget("LayoutBack").setGeometry(new WidgetGeometry(0.0, 80.0, 100.0, 20.0, CType.RELXY, CType.RELXY));
		this.getRoot().getWidget("LayoutBack").addWidget("ButtonBack", new ButtonScreenChange(this.getRoot().getWidget("LayoutBack"), "screen.button.back", this.parent));
		this.getRoot().getWidget("LayoutBack").getWidget("ButtonBack").setGeometry(new WidgetGeometry(50.0, 50.0, 100.0, 20.0, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
	}
}
