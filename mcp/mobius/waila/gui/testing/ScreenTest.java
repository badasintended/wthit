package mcp.mobius.waila.gui.testing;

import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.PictureDisplay;
import mcp.mobius.waila.gui.widgets.PictureSwitch;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonBoolean;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainerLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonInteger;
import mcp.mobius.waila.gui.widgets.buttons.ButtonIntegerConfig;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import net.minecraft.client.gui.GuiScreen;

public class ScreenTest extends ScreenBase {

	public ScreenTest(GuiScreen parent) {
		super(parent);
		this.getRoot().addWidget("Button", new ButtonScreenChange(this.getRoot(), "Next screen", new ScreenTest2(this)));
		this.getRoot().getWidget("Button").setGeometry(new WidgetGeometry(50.0, 50.0, 100.0, 20.0, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));
	}
}
