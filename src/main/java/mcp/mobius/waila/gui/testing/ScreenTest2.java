package mcp.mobius.waila.gui.testing;

import net.minecraft.client.gui.GuiScreen;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainerLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonIntegerConfig;

public class ScreenTest2 extends ScreenBase {

	public ScreenTest2(GuiScreen parent) {
		super(parent);
		//this.getRoot().addWidget("Picture", new PictureSwitch(this.getRoot(), "waila:textures/test.png", "waila:textures/test_solar.png"));
		//this.getRoot().addWidget("Button", new ButtonInteger(this.getRoot(), "Val0", "Val1", "Val2"));
		//this.getRoot().getWidget("Button").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, true, false, WidgetAlign.CENTER, WidgetAlign.CENTER));
		
		this.getRoot().addWidget("Buttons", new ButtonContainerLabel(this.getRoot(), 2, 100, 30.0));
		this.getRoot().getWidget("Buttons").setGeometry(new WidgetGeometry(0.0, 0.0, 100.0, 50.0,  CType.RELXY, CType.RELXY));
		
		//((ButtonContainer)this.getRoot().getWidget("Buttons")).addButton(new ButtonIntegerConfig(this.getRoot(), "testconfig", true, 0, "Val0", "Val1", "Val2"));
		
		this.getRoot().addWidget("Text", new LabelFixedFont(this.getRoot(), "waila:textures/test.png"));
		this.getRoot().getWidget("Text").setGeometry(new WidgetGeometry(50.0D, 10.0D, 50.0D, 50.0D,  CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.TOP));
	}	
	
}
