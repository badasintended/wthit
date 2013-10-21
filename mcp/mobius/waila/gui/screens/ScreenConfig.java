package mcp.mobius.waila.gui.screens;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.gui.interfaces.CoordType;
import mcp.mobius.waila.gui.interfaces.WidgetAlign;
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
		this.getRoot().getWidget("ButtonContainer").setGeometry(new WidgetGeometry(50.0, 50.0, 50.0, 30.0, CoordType.RELXY, CoordType.RELX, WidgetAlign.CENTER, WidgetAlign.CENTER));	
		
		ButtonContainer buttonContainer = ((ButtonContainer)this.getRoot().getWidget("ButtonContainer"));
		
		buttonContainer.addButton( new ButtonScreenChange(this.getRoot(), "Waila",   new ScreenWailaConfig(this)));
		buttonContainer.addButton( new ButtonScreenChange(this.getRoot(), "Modules", new ScreenModuleChoice(this)));
		
		this.getRoot().addWidget("LayoutBack", new LayoutBase(this.getRoot()));
		this.getRoot().getWidget("LayoutBack").setGeometry(new WidgetGeometry(0.0, 80.0, 100.0, 20.0, CoordType.RELXY, CoordType.RELXY));
		this.getRoot().getWidget("LayoutBack").addWidget("ButtonBack", new ButtonScreenChange(this.getRoot().getWidget("LayoutBack"), "Back", this.parent));
		this.getRoot().getWidget("LayoutBack").getWidget("ButtonBack").setGeometry(new WidgetGeometry(50.0, 50.0, 100.0, 20.0, CoordType.RELXY, CoordType.ABS, WidgetAlign.CENTER, WidgetAlign.CENTER));		
	}
}
