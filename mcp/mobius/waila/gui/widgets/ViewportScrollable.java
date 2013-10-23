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
		
		public Escalator(IWidget parent){
			this.parent = parent;
		}

		public void setOffset(int yoffset){
			this.yOffset = yoffset;
		}
		
		public void setMaxValue(int value){
			this.maxValue = value;
		}
		
		@Override
		public void draw(Point pos) {
			UIHelper.drawGradientRect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), 1, 0xff999999, 0xff999999);
			
			UIHelper.drawGradientRect(this.getLeft(), this.getTop() + (yOffset*-1), this.getRight(), this.getTop() + (yOffset*-1) + sizeCursor, 1, 0xffffffff, 0xffffffff);
		}
	}
	
	IWidget attachedWidget = null;
	int     yOffset        = 0;
	int		step		   = 5;

	public ViewportScrollable( IWidget parent){
		super(parent);
		this.addWidget("Cropping",  new LayoutCropping(null)).setGeometry(new WidgetGeometry(50.0, 50.0, 100.0, 100.0, CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
		this.addWidget("Escalator", new Escalator(null)).setGeometry(new WidgetGeometry(100.0, 0, 8, 100.0, CType.RELXY, CType.REL_Y, WAlign.RIGHT, WAlign.TOP)).hide();
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
		
	}
}
