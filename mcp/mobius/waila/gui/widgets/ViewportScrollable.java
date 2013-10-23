package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.Signal;
import mcp.mobius.waila.gui.interfaces.WAlign;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.Point;

public class ViewportScrollable extends WidgetBase {

	public class Escalator extends WidgetBase{
		int yOffset    = 0;
		int sizeCursor = 8;
		int maxValue   = 0;
		int step       = 0;
		boolean drag   = false;
		
		public Escalator(IWidget parent, int step){
			this.parent = parent;
			this.step   = step;
		}

		public void setOffset(int yoffset){
			this.yOffset = yoffset;
		}
		
		public void setMaxValue(int value){
			this.maxValue = value;
		}
		
		public void setStep(int step){
			this.step = step;
		}
		
		@Override
		public void draw(Point pos) {
			UIHelper.drawGradientRect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), 1, 0xff999999, 0xff999999);
			int offsetScaled = (int)(((double)this.getSize().getY() - (double)sizeCursor + 1) / (double)this.maxValue * (yOffset));
			UIHelper.drawGradientRect(this.getLeft(), this.getTop() + offsetScaled, this.getRight(), this.getTop() + offsetScaled + sizeCursor, 1, 0xffffffff, 0xffffffff);
		}
		
		@Override
		public void onMouseClick(MouseEvent event){
			if (event.button == 0){
				int offsetScaled = this.getTop() + (int)(((double)this.getSize().getY() - (double)sizeCursor + 1) / (double)this.maxValue * (yOffset));
				
				if (event.y < offsetScaled)
					this.yOffset += this.step;
				else if (event.y > offsetScaled + sizeCursor)
					this.yOffset -= this.step;
				else
					this.drag = true;
				
				this.emit(Signal.VALUE_CHANGED, this.yOffset);
				
			} else {
				super.onMouseClick(event);				
			}
		}
		
		@Override
		public void onMouseRelease(MouseEvent event){
			if (event.button == 0)
				this.drag = false;
			super.onMouseRelease(event);
		}
		
		@Override
		public void onMouseDrag(MouseEvent event){
			if (this.drag){
				int relativeY = (int)event.y - this.getTop();
				double factor = ((double)this.getSize().getY() - (double)sizeCursor + 1) / (double)this.maxValue;
				this.yOffset  = (int)(relativeY / factor);
				this.yOffset = Math.max(this.yOffset, this.maxValue);
				this.emit(Signal.VALUE_CHANGED, this.yOffset);
			}
			else
				super.onMouseDrag(event);
		}
		
	}
	
	IWidget attachedWidget = null;
	int     yOffset        = 0;
	int		step		   = 5;

	public ViewportScrollable( IWidget parent){
		super(parent);
		this.addWidget("Cropping",  new LayoutCropping(null)).setGeometry(new WidgetGeometry(0.0, 50.0, 80.0, 100.0, CType.RELXY, CType.REL_Y, WAlign.LEFT, WAlign.CENTER));
		this.addWidget("Escalator", new Escalator(null, this.step * 5)).setGeometry(new WidgetGeometry(100.0, 0, 8, 100.0, CType.RELXY, CType.REL_Y, WAlign.RIGHT, WAlign.TOP)).hide();
	}

	public IWidget attachWidget(IWidget widget){
		this.attachedWidget = this.getWidget("Cropping").addWidget("Cropped", widget);
		return this.attachedWidget;
	}

	public IWidget getAttachedWidget(){
		return this.attachedWidget;
	}
	
	public IWidget setStep(int step){
		this.step = step;
		((Escalator)this.getWidget("Escalator")).setStep(this.step * 5);
		return this;
	}
	
	@Override
	public void draw(Point pos) {}
	
	@Override
	public void draw(){
		//if (Display.wasResized())
		//	System.out.println("True");
		
		if ((this.attachedWidget != null) && (this.attachedWidget.getSize().getY() > this.getSize().getY()))
			this.getWidget("Escalator").show();
		else
			this.getWidget("Escalator").hide();		
		
		super.draw();
	}
	
	@Override
	public void onMouseWheel(MouseEvent event){
		this.yOffset += event.z / 120.0 * this.step;

		this.yOffset = Math.max(this.yOffset,  this.getSize().getY() - this.attachedWidget.getSize().getY());
		this.yOffset = Math.min(this.yOffset, 0);
		
		((LayoutCropping)this.getWidget("Cropping")).setOffsets(0, this.yOffset);
		((Escalator)this.getWidget("Escalator")).setOffset(this.yOffset);
	}
	
	@Override
	public void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params){
		if (srcwidget.equals(this.attachedWidget) && signal == Signal.GEOM_CHANGED){
			((Escalator)this.getWidget("Escalator")).setMaxValue(this.getSize().getY() - srcwidget.getSize().getY());
		}

		if (srcwidget.equals(this.getWidget("Escalator")) && signal == Signal.VALUE_CHANGED){
			this.yOffset = (Integer)params[0];
			((LayoutCropping)this.getWidget("Cropping")).setOffsets(0, this.yOffset);
		}		
		
	}
	
	@Override
	public void onMouseDrag(MouseEvent event){
		if (this.getWidget("Escalator").shouldRender() && ((Escalator)this.getWidget("Escalator")).drag)
			this.getWidget("Escalator").onMouseDrag(event);
		else
			super.onMouseDrag(event);
	}

	@Override
	public void onMouseClick(MouseEvent event){
		if (event.button == 0){
			if (!this.getWidget("Escalator").isWidgetAtCoordinates(event.x, event.y)){
				((Escalator)this.getWidget("Escalator")).drag = false;
				super.onMouseClick(event);
			}
			else
				this.getWidget("Escalator").onMouseClick(event);
		}
		else
			super.onMouseClick(event);
	}	
	
}
