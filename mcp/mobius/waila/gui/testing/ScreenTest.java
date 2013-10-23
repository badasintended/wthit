package mcp.mobius.waila.gui.testing;

import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.ItemStackDisplay;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutCropping;
import mcp.mobius.waila.gui.widgets.PictureDisplay;
import mcp.mobius.waila.gui.widgets.PictureSwitch;
import mcp.mobius.waila.gui.widgets.WidgetBase;
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

	public class SubLayout extends WidgetBase{
		
		IWidget attachedWidget = null;
		int     yOffset        = 0;
		
		public SubLayout( IWidget parent){
			super(parent);
			this.addWidget("Cropping", new LayoutCropping(null)).setGeometry(new WidgetGeometry(50.0,50.0,100.0,100.0,CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
		}

		public IWidget attachWidget(IWidget widget){
			this.attachedWidget = this.getWidget("Cropping").addWidget("Cropped", widget);
			return this.attachedWidget;
		}

		public IWidget getAttachedWidget(){
			return this.attachedWidget;
		}
		
		@Override
		public void draw(Point pos) {}
		
		@Override
		public void onMouseWheel(MouseEvent event){
			this.yOffset += event.z / 120.0;
			System.out.println(this.yOffset);
			((LayoutCropping)this.getWidget("Cropping")).setOffsets(0, this.yOffset);
		}
	}
	
	public ScreenTest(GuiScreen parent) {
		super(parent);
		
		this.getRoot().addWidget("Layout", new SubLayout(null)).setGeometry(new WidgetGeometry(50.0,50.0,50.0,50.0,CType.ABSXY, CType.RELXY, WAlign.TOP, WAlign.LEFT));
		((SubLayout)this.getRoot().getWidget("Layout")).attachWidget(new PictureSwitch(this.getRoot(), "waila:textures/test.png", "waila:textures/test_solar.png")).getGeometry().setSize(300, 300);
		
		//this.getRoot().addWidget("Layout", new LayoutCropping(null)).setGeometry(new WidgetGeometry(50.0,50.0,50.0,50.0,CType.ABSXY, CType.RELXY, WAlign.TOP, WAlign.LEFT));
		//this.getRoot().getWidget("Layout").addWidget("Picture", new PictureSwitch(this.getRoot(), "waila:textures/test.png", "waila:textures/test_solar.png")).getGeometry().setSize(300, 300);
		//this.getRoot().addWidget("Picture", new ItemStackDisplay(null, new ItemStack(Block.dirt, 8))).setGeometry(new WidgetGeometry(50.0,50.0,75.0,75.0,CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
	}
}
