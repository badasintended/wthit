package mcp.mobius.waila.gui.screens.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonBooleanConfig;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainerLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonIntegerConfig;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import mcp.mobius.waila.utils.Constants;

public class ScreenWailaConfig extends ScreenBase {
	public ScreenWailaConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("ButtonContainer", new ButtonContainerLabel(this.getRoot(), 2, 100, 25.0));
		this.getRoot().getWidget("ButtonContainer").setGeometry(new WidgetGeometry(0.0, 10.0, 100.0, 50.0,  CType.RELXY, CType.RELXY));	
		
		ButtonContainerLabel buttonContainer = ((ButtonContainerLabel)this.getRoot().getWidget("ButtonContainer"));
		
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW,     true, true, "screen.button.hidden",     "screen.button.visible"), "choice.showhidewaila");
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE,     true, true, "screen.button.maintained", "screen.button.toggled"), "choice.toggledmaintained");
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true, false,"screen.button.hidden",     "screen.button.visible"), "choice.showhideidmeta");
		buttonContainer.addButton( new ButtonBooleanConfig(this.getRoot(), Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID,   true, false,"screen.button.hidden",     "screen.button.visible"), "choice.showliquids");
		

		this.getRoot().addWidget("LayoutConfigPos", new LayoutBase(this.getRoot()));
		this.getRoot().getWidget("LayoutConfigPos").setGeometry(new WidgetGeometry(0.0, 50.0, 100.0, 20.0,  CType.RELXY, CType.RELXY));
		this.getRoot().getWidget("LayoutConfigPos").addWidget("ButtonConfigPos", new ButtonScreenChange(null, "screen.button.configureaspect", new ScreenHUDConfig(this)));
		this.getRoot().getWidget("LayoutConfigPos").getWidget("ButtonConfigPos").setGeometry(new WidgetGeometry(50.0, 50.0, 150.0, 20.0, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
		
		this.getRoot().addWidget("LayoutBack", new LayoutBase(this.getRoot()));
		this.getRoot().getWidget("LayoutBack").setGeometry(new WidgetGeometry(0.0, 80.0, 100.0, 20.0,  CType.RELXY, CType.RELXY));
		this.getRoot().getWidget("LayoutBack").addWidget("ButtonBack", new ButtonScreenChange(null, "screen.button.back", this.parent));
		this.getRoot().getWidget("LayoutBack").getWidget("ButtonBack").setGeometry(new WidgetGeometry(50.0, 50.0, 100.0, 20.0, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
	}
}
