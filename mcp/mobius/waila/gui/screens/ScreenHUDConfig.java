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
			
			this.addWidget("TextTuto1", new LabelFixedFont(null, "Drag the HUD to setup its position.")).setGeometry(new WidgetGeometry(50.0,  30.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));;
			
			IWidget layoutX = this.addWidget("LayoutX", new LayoutBase(null));
			layoutX.setGeometry(new WidgetGeometry(45.0,50.0,20,60, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			//layoutX.addWidget("ButtonXAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
			//layoutX.addWidget("ButtonXSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.BOTTOM));
			layoutX.addWidget("TextX",   new LabelFixedFont(null, "X :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			
			IWidget layoutY = this.addWidget("LayoutY", new LayoutBase(this));
			layoutY.setGeometry(new WidgetGeometry(55.0,50.0,20,60,CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));			
			//layoutY.addWidget("ButtonYAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
			//layoutY.addWidget("ButtonYSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.BOTTOM));		
			layoutY.addWidget("TextY",   new LabelFixedFont(null, "Y :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			
			this.addWidget("ButtonCancel", new ButtonScreenChange(null, "Cancel", prevScreen)).setGeometry(new WidgetGeometry(70.0,97.0,100,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));
			this.addWidget("ButtonOk",     new ButtonScreenChange(null, "Ok",     prevScreen)).setGeometry(new WidgetGeometry(30.0,97.0,100,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));
		
		}		
		
		@Override
		public IWidget getWidgetAtCoordinates(double posX, double posY){
			if (this.getWidget("Layout").getWidget("Picture").isWidgetAtCoordinates(posX, posY)){
				return this.getWidget("Layout").getWidget("Picture");
			} else
				return super.getWidgetAtCoordinates(posX, posY);
		}
		
		@Override 
		public void onMouseClick(MouseEvent event){
			if (event.button == 0)
				//if ((this.getWidgetAtCoordinates(event.x, event.y) == this))
					if (this.getWidget("Layout").getWidget("Picture").isWidgetAtCoordinates(event.x, event.y)){
						this.getWidget("Layout").getWidget("Picture").onMouseClick(event);
						this.draggedWidget = this.getWidget("Layout").getWidget("Picture");
					} 
					else
						super.onMouseClick(event);
			else
				super.onMouseClick(event);
		}
		
		@Override	
		public void onMouseDrag(MouseEvent event){
			
			//if ((this.getWidgetAtCoordinates(event.x, event.y) == this) && (this.draggedWidget != null)){
			if (this.draggedWidget != null){
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
				((LabelFixedFont)this.getWidget("LayoutX").getWidget("TextX")).setText(String.format("X : %.2f", this.draggedWidget.getGeometry().getRawPos(this.getWidget("Layout")).getX()));
				((LabelFixedFont)this.getWidget("LayoutY").getWidget("TextY")).setText(String.format("Y : %.2f", this.draggedWidget.getGeometry().getRawPos(this.getWidget("Layout")).getY()));		
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
