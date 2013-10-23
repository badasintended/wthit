package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;

import org.lwjgl.util.Point;

public class ViewportScrollable extends WidgetBase {

	public class Escalator extends WidgetBase{
		public Escalator(IWidget parent){
			this.parent = parent;
		}
		
		@Override
		public void draw(Point pos) {}
	}
	
	IWidget attachedWidget = null;
	int     yOffset        = 0;
	int		step		   = 5;
	
	public ViewportScrollable( IWidget parent){
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
	
	public IWidget setStep(int step){
		this.step = step;
		return this;
	}
	
	@Override
	public void draw(Point pos) {}
	
	@Override
	public void onMouseWheel(MouseEvent event){
		this.yOffset += event.z / 120.0 * this.step;

		this.yOffset = Math.max(this.yOffset,  this.getSize().getY() - this.attachedWidget.getSize().getY());
		this.yOffset = Math.min(this.yOffset, 0);
		
		((LayoutCropping)this.getWidget("Cropping")).setOffsets(0, this.yOffset);
	}
}
