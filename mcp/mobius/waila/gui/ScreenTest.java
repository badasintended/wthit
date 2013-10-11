package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.PictureDisplay;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import net.minecraft.client.gui.GuiScreen;

public class ScreenTest extends ScreenBase {

	public ScreenTest(GuiScreen parent) {
		super(parent);
		this.getRoot().addWidget("Picture", new PictureDisplay(this.getRoot(), "waila:textures/test.png"));
		this.getRoot().getWidget("Picture").setGeometry(new WidgetGeometry(50.0, 50.0, 25.0, 25.0, true, true, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));
		
		this.getRoot().addWidget("Text", new LabelFixedFont(this.getRoot(), "waila:textures/test.png"));
		this.getRoot().getWidget("Text").setGeometry(new WidgetGeometry(50.0D, 0.0D, 50, 50, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
	}
}
