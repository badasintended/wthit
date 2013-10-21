package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;

import org.lwjgl.util.Point;

public class LayoutMargin extends WidgetBase {

	int left   = 0;
	int right  = 0;
	int top    = 0;
	int bottom = 0;
	
	public LayoutMargin(IWidget parent){
		super (parent);
		this.setGeometry(new WidgetGeometry(0,0,0,0,CType.ABSXY,CType.ABSXY,WAlign.CENTER,WAlign.CENTER));
	}
	
	public void setMargins(int left, int right, int top, int bottom){
		this.left   = left;
		this.right  = right;
		this.top    = top;
		this.bottom = bottom;
	}
	
	@Override
	public Point getSize(){
		Point parentSize = this.parent.getSize();
		return new Point(parentSize.getX() - left - right, parentSize.getY() - top - bottom);
	}

	@Override
	public Point getPos(){
		Point parentPos = this.parent.getPos();
		return new Point(parentPos.getX() + left, parentPos.getY() + top);
	}	
	
	@Override
	public void draw(Point pos) {}

}
