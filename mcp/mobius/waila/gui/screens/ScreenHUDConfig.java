package mcp.mobius.waila.gui.screens;

import org.lwjgl.util.Point;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.addons.ConfigHandler;
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
			
			this.addWidget("TextTuto1", new LabelFixedFont(null, "Drag the HUD to setup its position.")).setGeometry(new WidgetGeometry(50.0,  30.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));;
			
			IWidget layoutX = this.addWidget("LayoutX", new LayoutBase(this));
			layoutX.setGeometry(new WidgetGeometry(45.0,50.0,20,60, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			layoutX.addWidget("ButtonXAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
			layoutX.addWidget("ButtonXSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.BOTTOM));
			layoutX.addWidget("TextX",   new LabelFixedFont(null, "X :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			
			IWidget layoutY = this.addWidget("LayoutY", new LayoutBase(this));
			layoutY.setGeometry(new WidgetGeometry(55.0,50.0,20,60,CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));			
			layoutY.addWidget("ButtonYAdd", new ButtonLabel(null, "+")).setGeometry(new WidgetGeometry(0.0,  0.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));
			layoutY.addWidget("ButtonYSub", new ButtonLabel(null, "-")).setGeometry(new WidgetGeometry(0.0,100.0,20,20, CType.RELXY, CType.ABSXY, WAlign.LEFT, WAlign.BOTTOM));		
			layoutY.addWidget("TextY",   new LabelFixedFont(null, "Y :   0")).setGeometry(new WidgetGeometry(50.0,50.0,20,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
			
			this.addWidget("ButtonCancel", new ButtonScreenChange(null, "Cancel", prevScreen)).setGeometry(new WidgetGeometry(70.0,97.0,75,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));
			this.addWidget("ButtonOk",     new ButtonScreenChange(null, "Ok",     prevScreen)).setGeometry(new WidgetGeometry(30.0,97.0,75,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));
			this.addWidget("ButtonDefault",new ButtonLabel(null, "Default")).setGeometry(new WidgetGeometry(50.0,97.0,75,20, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.BOTTOM));
			
			double picX  = ConfigHandler.instance().getConfigInt(Constants.CFG_WAILA_POSX) / 100.0;
			double picY  = ConfigHandler.instance().getConfigInt(Constants.CFG_WAILA_POSY) / 100.0;
			int picSX = 180 / this.rez.getScaleFactor(), picSY = 62 / this.rez.getScaleFactor();
			this.addWidget("Layout", new LayoutMargin(null));
			((LayoutMargin)this.getWidget("Layout")).setMargins(picSX/2, picSX/2, picSY/2, picSY/2);
			this.getWidget("Layout").addWidget("Picture", new PictureMovableRC(null, "waila:textures/config_template.png")).setGeometry(new WidgetGeometry(picX, picY, picSX, picSY,CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));			

			((LabelFixedFont)this.getWidget("LayoutX").getWidget("TextX")).setText(String.format("X : %.2f", picX));
			((LabelFixedFont)this.getWidget("LayoutY").getWidget("TextY")).setText(String.format("Y : %.2f", picY));			
			
		}		
		
		@Override
		public IWidget getWidgetAtCoordinates(double posX, double posY){
			if (this.getWidget("Layout").getWidget("Picture").isWidgetAtCoordinates(posX, posY))
				return this.getWidget("Layout").getWidget("Picture");
			else
				return super.getWidgetAtCoordinates(posX, posY);
		}
		
		@Override 
		public void onMouseClick(MouseEvent event){
			if ((event.button == 0) && (this.getWidget("Layout").getWidget("Picture").isWidgetAtCoordinates(event.x, event.y))){ 
				this.getWidget("Layout").getWidget("Picture").onMouseClick(event);
				this.draggedWidget = this.getWidget("Layout").getWidget("Picture");
			} 
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
				((LabelFixedFont)this.getWidget("LayoutX").getWidget("TextX")).setText(String.format("X : %.2f", this.draggedWidget.getGeometry().getRawPos().getX()));
				((LabelFixedFont)this.getWidget("LayoutY").getWidget("TextY")).setText(String.format("Y : %.2f", this.draggedWidget.getGeometry().getRawPos().getY()));		
			}

			if (signal == Signal.CLICKED){
			
				IWidget picture = this.getWidget("Layout").getWidget("Picture");
				double  pictureX = picture.getGeometry().getRawPos().getX();
				double  pictureY = picture.getGeometry().getRawPos().getY();
				double  pixelToPercentX = 100.0D / picture.getParent().getSize().getX();
				double  pixelToPercentY = 100.0D / picture.getParent().getSize().getY();
				
				if (srcwidget.equals(this.getWidget("LayoutX").getWidget("ButtonXAdd")))
					picture.getGeometry().setPos(pictureX + pixelToPercentX, pictureY);
				
				if (srcwidget.equals(this.getWidget("LayoutX").getWidget("ButtonXSub")))
					picture.getGeometry().setPos(pictureX - pixelToPercentX, pictureY);
				
				if (srcwidget.equals(this.getWidget("LayoutY").getWidget("ButtonYAdd")))
					picture.getGeometry().setPos(pictureX, pictureY + pixelToPercentY);
				
				if (srcwidget.equals(this.getWidget("LayoutY").getWidget("ButtonYSub")))
					picture.getGeometry().setPos(pictureX, pictureY - pixelToPercentY);

				picture.emit(Signal.DRAGGED, picture.getPos());
				
			}
			if (srcwidget.equals(this.getWidget("ButtonOk")) && signal == Signal.CLICKED){
				ConfigHandler.instance().setConfigInt(Constants.CFG_WAILA_POSX, (int)(this.getWidget("Layout").getWidget("Picture").getGeometry().getRawPos().getX() * 100.0));
				ConfigHandler.instance().setConfigInt(Constants.CFG_WAILA_POSY, (int)(this.getWidget("Layout").getWidget("Picture").getGeometry().getRawPos().getY() * 100.0));				
			}
			
			if (srcwidget.equals(this.getWidget("ButtonDefault")) && signal == Signal.CLICKED){
				this.getWidget("Layout").getWidget("Picture").getGeometry().setPos(50.0, 1.0);
			}
		}
	}
	
	public ScreenHUDConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("EventCanvas", new EventCanvas(this.getRoot(), parent));
	}

}
