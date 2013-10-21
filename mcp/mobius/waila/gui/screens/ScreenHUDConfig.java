package mcp.mobius.waila.gui.screens;

import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.Signal;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.LayoutMargin;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonLabel;
import mcp.mobius.waila.gui.widgets.buttons.ButtonScreenChange;
import mcp.mobius.waila.gui.widgets.movable.PictureMovableRC;
import net.minecraft.client.gui.GuiScreen;

public class ScreenHUDConfig extends ScreenBase {

	private class EventCanvas extends LayoutBase{
		IWidget draggedWidget = null;
		
		public EventCanvas(IWidget parent, GuiScreen prevScreen){
			super(parent);
			this.setGeometry(new WidgetGeometry(0.0,0.0,100.0,100.0,CType.RELXY, CType.RELXY));
			
			this.addWidget("Layout", new LayoutMargin(null));
			((LayoutMargin)this.getWidget("Layout")).setMargins(25, 25, 25, 25);
			this.getWidget("Layout").addWidget("Picture", new PictureMovableRC(null, "waila:textures/test.png")).setGeometry(new WidgetGeometry(50.0, 50.0, 50.0, 50.0,CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			
			/*
			this.addWidget("TextTuto1", new LabelFixedFont(null, "Either drag the tooltip or use the buttons below to adjust your HUD")).setGeometry(new WidgetGeometry(50.0,  30.0,20,20, true, true, false, false, WidgetAlign.CENTER, WidgetAlign.CENTER));;
			
			IWidget layoutX = this.addWidget("LayoutX", new LayoutBase(null));
			layoutX.setGeometry(new WidgetGeometry(45.0,50.0,20,60,true, true, false, false, WidgetAlign.CENTER, WidgetAlign.CENTER));
			layoutX.addWidget("ButtonXAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, true, true, false, false, WidgetAlign.LEFT, WidgetAlign.TOP));
			layoutX.addWidget("ButtonXSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, true, true, false, false, WidgetAlign.LEFT, WidgetAlign.BOTTOM));
			layoutX.addWidget("TextX",   new LabelFixedFont(null, "X :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, true, true, false, false, WidgetAlign.CENTER, WidgetAlign.CENTER));
			
			IWidget layoutY = this.addWidget("LayoutY", new LayoutBase(this));
			layoutY.setGeometry(new WidgetGeometry(55.0,50.0,20,60,true, true, false, false, WidgetAlign.CENTER, WidgetAlign.CENTER));			
			layoutY.addWidget("ButtonYAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, true, true, false, false, WidgetAlign.LEFT, WidgetAlign.TOP));
			layoutY.addWidget("ButtonYSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, true, true, false, false, WidgetAlign.LEFT, WidgetAlign.BOTTOM));		
			layoutY.addWidget("TextY",   new LabelFixedFont(null, "Y :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, true, true, false, false, WidgetAlign.CENTER, WidgetAlign.CENTER));
			
			this.addWidget("ButtonCancel", new ButtonScreenChange(null, "Cancel", prevScreen)).setGeometry(new WidgetGeometry(70.0,97.0,100,20, true, true, false, false, WidgetAlign.CENTER, WidgetAlign.BOTTOM));;
			this.addWidget("ButtonOk",     new ButtonScreenChange(null, "Ok",     prevScreen)).setGeometry(new WidgetGeometry(30.0,97.0,100,20, true, true, false, false, WidgetAlign.CENTER, WidgetAlign.BOTTOM));;;
			*/			
		}		
		
		@Override 
		public void onMouseClick(MouseEvent event){
			if (event.button == 0)
				if ((this.getWidgetAtCoordinates(event.x, event.y) == this))
					if (this.getWidget("Layout").getWidget("Picture").isWidgetAtCoordinates(event.x, event.y)){
						this.getWidget("Layout").getWidget("Picture").onMouseClick(event);
						this.draggedWidget = this.getWidget("Layout").getWidget("Picture");
					}
			super.onMouseClick(event);
		}
		
		@Override	
		public void onMouseDrag(MouseEvent event){
			if ((this.getWidgetAtCoordinates(event.x, event.y) == this) && (this.draggedWidget != null)){
				this.draggedWidget.onMouseDrag(event);
			} else {
				super.onMouseDrag(event);
			}
		}
		
		@Override
		public void onMouseRelease(MouseEvent event){
			if (event.button == 0)
				this.draggedWidget = null;
			super.onMouseRelease(event);
		}
		
		@Override
		public void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params){
			if (srcwidget.equals(this.getWidget("Layout").getWidget("Picture")) && signal == Signal.DRAGGED){
				this.draggedWidget = this.getWidget("Layout").getWidget("Picture");
				//((LabelFixedFont)this.getWidget("LayoutX").getWidget("TextX")).setText(String.format("X : %3d", ((Point)params[0]).getX()));
				//((LabelFixedFont)this.getWidget("LayoutY").getWidget("TextY")).setText(String.format("Y : %3d", ((Point)params[0]).getY()));				
			}

			/*
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
			
			if (srcwidget.equals(this.getWidget("ButtonOk")) && signal == Signal.CLICKED){
				
			}
			*/
		}
	}
	
	public ScreenHUDConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("EventCanvas", new EventCanvas(this.getRoot(), parent));
	}

}
