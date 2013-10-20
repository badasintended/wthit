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
			
			this.addWidget("Picture", new PictureMovable(null, "waila:textures/test.png"));
			
			IWidget layoutX = this.addWidget("LayoutX", new LayoutBase(null));
			layoutX.setGeometry(new WidgetGeometry(45.0,50.0,20,60,true, true, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));
			layoutX.addWidget("ButtonXAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, true, true, false, false, WidgetGeometry.Align.LEFT, WidgetGeometry.Align.TOP));
			layoutX.addWidget("ButtonXSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, true, true, false, false, WidgetGeometry.Align.LEFT, WidgetGeometry.Align.BOTTOM));
			layoutX.addWidget("TextX",   new LabelFixedFont(null, "X :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, true, true, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));
			
			IWidget layoutY = this.addWidget("LayoutY", new LayoutBase(this));
			layoutY.setGeometry(new WidgetGeometry(55.0,50.0,20,60,true, true, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));			
			layoutY.addWidget("ButtonYAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, true, true, false, false, WidgetGeometry.Align.LEFT, WidgetGeometry.Align.TOP));
			layoutY.addWidget("ButtonYSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, true, true, false, false, WidgetGeometry.Align.LEFT, WidgetGeometry.Align.BOTTOM));		
			layoutY.addWidget("TextY",   new LabelFixedFont(null, "Y :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, true, true, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));
		}		
		
		@Override
		public void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params){
			if (srcwidget.equals(this.getWidget("Picture")) && signal == Signal.DRAGGED){
				((LabelFixedFont)this.getWidget("LayoutX").getWidget("TextX")).setText(String.format("X : %3d", ((Point)params[0]).getX()));
				((LabelFixedFont)this.getWidget("LayoutY").getWidget("TextY")).setText(String.format("Y : %3d", ((Point)params[0]).getY()));				
			}
			
			if (srcwidget.equals(this.getWidget("LayoutX").getWidget("ButtonXAdd")) && signal == Signal.CLICKED){
				this.getWidget("Picture").getGeometry().setPos(this.getWidget("Picture").getLeft() + 1, this.getWidget("Picture").getTop());
				this.getWidget("Picture").emit(Signal.DRAGGED, this.getWidget("Picture").getPos());
			}
			
			if (srcwidget.equals(this.getWidget("LayoutX").getWidget("ButtonXSub")) && signal == Signal.CLICKED){
				this.getWidget("Picture").getGeometry().setPos(this.getWidget("Picture").getLeft() - 1, this.getWidget("Picture").getTop());
				this.getWidget("Picture").emit(Signal.DRAGGED, this.getWidget("Picture").getPos());
			}
			
			if (srcwidget.equals(this.getWidget("LayoutY").getWidget("ButtonYAdd")) && signal == Signal.CLICKED){
				this.getWidget("Picture").getGeometry().setPos(this.getWidget("Picture").getLeft(), this.getWidget("Picture").getTop() + 1);
				this.getWidget("Picture").emit(Signal.DRAGGED, this.getWidget("Picture").getPos());
			}
			
			if (srcwidget.equals(this.getWidget("LayoutY").getWidget("ButtonYSub")) && signal == Signal.CLICKED){
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
