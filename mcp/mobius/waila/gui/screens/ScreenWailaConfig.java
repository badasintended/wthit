package mcp.mobius.waila.gui.screens;

import net.minecraft.client.gui.GuiScreen;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonBooleanConfig;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainerLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonIntegerConfig;

public class ScreenWailaConfig extends ScreenBase {
	public ScreenWailaConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("ButtonContainer", new ButtonContainerLabel(this.getRoot(), 2, 100, 30.0));
		this.getRoot().getWidget("ButtonContainer").setGeometry(new WidgetGeometry(0.0, 0.0, 100.0, 50.0, true, true));	
		
		ButtonContainerLabel buttonContainer = ((ButtonContainerLabel)this.getRoot().getWidget("ButtonContainer"));
		
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Constants.CFG_WAILA_SHOW,     true, true, "Hidden",     "Visible"), "Show/Hide Waila");
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Constants.CFG_WAILA_MODE,     true, true, "Maintained", "Toggled"), "Toggled/Maintained");
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Constants.CFG_WAILA_METADATA, true, false,"Hidden",     "Visible"), "Show ID:Metadata");
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Constants.CFG_WAILA_LIQUID,   true, false,"Hidden",     "Visible"), "Liquids");
	}
}
