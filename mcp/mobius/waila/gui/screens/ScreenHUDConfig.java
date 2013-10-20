package mcp.mobius.waila.gui.screens;

import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.Signal;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.PictureMovable;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonLabel;
import net.minecraft.client.gui.GuiScreen;

public class ScreenHUDConfig extends ScreenBase {

	private class EventCanvas extends LayoutBase{
		public EventCanvas(IWidget parent){
			super(parent);
			this.setGeometry(new WidgetGeometry(0.0,0.0,100.0,100.0,true,true));
			
			this.addWidget("Picture", new PictureMovable(this, "waila:textures/test.png"));
			
			this.addWidget("ButtonXAdd", new ButtonLabel(this, "+"));
			this.getWidget("ButtonXAdd").setGeometry(new WidgetGeometry(50.0,60,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
			this.addWidget("ButtonXSub", new ButtonLabel(this, "-"));
			this.getWidget("ButtonXSub").setGeometry(new WidgetGeometry(50.0,80,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));

			this.addWidget("ButtonYAdd", new ButtonLabel(this, "+"));
			this.getWidget("ButtonYAdd").setGeometry(new WidgetGeometry(55.0,60,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
			this.addWidget("ButtonYSub", new ButtonLabel(this, "-"));
			this.getWidget("ButtonYSub").setGeometry(new WidgetGeometry(55.0,80,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));		
			
			this.addWidget("Text",   new LabelFixedFont(this, "None"));
			this.getWidget("Text").setGeometry(new WidgetGeometry(50.0,0,0,0,true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
			((LabelFixedFont)this.getWidget("Text")).setText(String.format("X : %3d, Y : %3d", 0, 0));
			
		}		
		
		@Override
		public void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params){
			if (srcwidget.equals(this.getWidget("Picture")) && signal == Signal.DRAGGED)
				((LabelFixedFont)this.getWidget("Text")).setText(String.format("X : %3d, Y : %3d", ((Point)params[0]).getX(), ((Point)params[0]).getY()));
			
			if (srcwidget.equals(this.getWidget("ButtonXAdd")) && signal == Signal.CLICKED){
				this.getWidget("Picture").getGeometry().setPos(this.getWidget("Picture").getLeft() + 1, this.getWidget("Picture").getTop());
				this.getWidget("Picture").emit(Signal.DRAGGED, this.getWidget("Picture").getPos());
			}
			
			if (srcwidget.equals(this.getWidget("ButtonXSub")) && signal == Signal.CLICKED){
				this.getWidget("Picture").getGeometry().setPos(this.getWidget("Picture").getLeft() - 1, this.getWidget("Picture").getTop());
				this.getWidget("Picture").emit(Signal.DRAGGED, this.getWidget("Picture").getPos());
			}
			
			if (srcwidget.equals(this.getWidget("ButtonYAdd")) && signal == Signal.CLICKED){
				this.getWidget("Picture").getGeometry().setPos(this.getWidget("Picture").getLeft(), this.getWidget("Picture").getTop() + 1);
				this.getWidget("Picture").emit(Signal.DRAGGED, this.getWidget("Picture").getPos());
			}
			
			if (srcwidget.equals(this.getWidget("ButtonYSub")) && signal == Signal.CLICKED){
				this.getWidget("Picture").getGeometry().setPos(this.getWidget("Picture").getLeft(), this.getWidget("Picture").getTop() - 1);
				this.getWidget("Picture").emit(Signal.DRAGGED, this.getWidget("Picture").getPos());
			}			
		}
	}
	
	public ScreenHUDConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("EventCanvas", new EventCanvas(this.getRoot()));
	}

}
