package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.PictureDisplay;
import mcp.mobius.waila.gui.widgets.PictureSwitch;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonBoolean;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainer;
import mcp.mobius.waila.gui.widgets.buttons.ButtonInteger;
import mcp.mobius.waila.gui.widgets.buttons.ButtonIntegerConfig;
import net.minecraft.client.gui.GuiScreen;

public class ScreenTest extends ScreenBase {

	public ScreenTest(GuiScreen parent) {
		super(parent);
		//this.getRoot().addWidget("Picture", new PictureSwitch(this.getRoot(), "waila:textures/test.png", "waila:textures/test_solar.png"));
		//this.getRoot().addWidget("Button", new ButtonInteger(this.getRoot(), "Val0", "Val1", "Val2"));
		//this.getRoot().getWidget("Button").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));
		
		this.getRoot().addWidget("Buttons", new ButtonContainer(this.getRoot(), 2, 100, 30.0));
		this.getRoot().getWidget("Buttons").setGeometry(new WidgetGeometry(0.0, 0.0, 100.0, 50.0, true, true));
		
		((ButtonContainer)this.getRoot().getWidget("Buttons")).addButton(new ButtonIntegerConfig(this.getRoot(), "testconfig", true, 0, "Val0", "Val1", "Val2"));
		
		this.getRoot().addWidget("Text", new LabelFixedFont(this.getRoot(), "waila:textures/test.png"));
		this.getRoot().getWidget("Text").setGeometry(new WidgetGeometry(50.0D, 10.0D, 50.0D, 50.0D, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
	}
}
