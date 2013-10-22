package mcp.mobius.waila.gui.testing;

import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.ItemStackDisplay;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutCropping;
import mcp.mobius.waila.gui.widgets.PictureDisplay;
import mcp.mobius.waila.gui.widgets.PictureSwitch;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonBoolean;
import mcp.mobius.waila.gui.widgets.buttons.ButtonContainerLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonInteger;
import mcp.mobius.waila.gui.widgets.buttons.ButtonIntegerConfig;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import mcp.mobius.waila.gui.widgets.movable.PictureMovableRC;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class ScreenTest extends ScreenBase {

	public ScreenTest(GuiScreen parent) {
		super(parent);
		
		this.getRoot().addWidget("Layout", new LayoutCropping(null)).setGeometry(new WidgetGeometry(50.0,50.0,50.0,50.0,CType.ABSXY, CType.RELXY, WAlign.TOP, WAlign.LEFT));
		this.getRoot().getWidget("Layout").addWidget("Picture", new PictureSwitch(this.getRoot(), "waila:textures/test.png", "waila:textures/test_solar.png")).getGeometry().setSize(300, 300);
		//this.getRoot().addWidget("Picture", new ItemStackDisplay(null, new ItemStack(Block.dirt, 8))).setGeometry(new WidgetGeometry(50.0,50.0,75.0,75.0,CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
	}
}
