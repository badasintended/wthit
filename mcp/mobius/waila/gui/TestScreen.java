package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.widgets.PictureDisplay;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import net.minecraft.client.gui.GuiScreen;

public class TestScreen extends BaseScreen {

	public TestScreen(GuiScreen parent) {
		super(parent);
		this.getRoot().addWidget("Picture", new PictureDisplay(this.getRoot(), "waila:textures/test.png"));
		//this.getRoot().getWidget("Picture").setGeometry(new WidgetGeometry(0.0, 0.0, 50.0, 50.0));
		this.getRoot().getWidget("Picture").setGeometry(new WidgetGeometry(50.0, 50.0, 50.0, 50.0, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));
	}
}
