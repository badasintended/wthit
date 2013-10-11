package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.util.MathHelper;

import org.lwjgl.util.Point;



public class WidgetGeometry {
	
	public enum Align {LEFT, CENTER, RIGHT, TOP, BOTTOM};
	
	double relX = -1;
	double relY = -1;
	int    absX = -1;
	int    absY = -1;

	double relSX = -1;
	double relSY = -1;
	int    absSX = -1;
	int    absSY = -1;	

	boolean isFractional;
	
	Align alignX;
	Align alignY;
	
	public class PointDouble{
		double x; double y;
		public PointDouble(double x, double y){ this.x = x; this.y = y;};
	}	
	
	public WidgetGeometry(double x, double y, double sx, double sy){
		this.relX  = x;
		this.relY  = y;
		this.relSX = sx;
		this.relSY = sy;
		this.isFractional = false;
		this.alignX = Align.LEFT;
		this.alignY = Align.TOP;
	}

	public WidgetGeometry(double x, double y, double sx, double sy, Align alignX, Align alignY){
		this.relX  = x;
		this.relY  = y;
		this.relSX = sx;
		this.relSY = sy;
		this.isFractional = false;
		this.alignX = alignX;
		this.alignY = alignY;
	}	
	
	public WidgetGeometry(int x, int y, int sx, int sy){
		this.absX  = x;
		this.absY  = y;
		this.absSX = sx;
		this.absSY = sy;
		this.isFractional = true;
		this.alignX = Align.LEFT;
		this.alignY = Align.TOP;		
	}	
	
	//public PointDouble getRelativePos() { return new PointDouble(this.relX, this.relY);}
	//public Point       getAbsolutePos() { return new Point(this.absX, this.absY);}
	//public PointDouble getRelativeSize(){ return new PointDouble(this.relSX, this.relSY);}
	//public Point       getAbsoluteSize(){ return new Point(this.absSX, this.absSY);}
	
	public Point getPos(IWidget parent){
		Point thisPos;
		
		if (this.isFractional){
			Point parentPos  = parent.getPos();
			thisPos = new Point(parentPos.getX() + this.absX, parentPos.getY() +this.absY);
		}
		else {
			Point parentPos  = parent.getPos();
			Point parentSize = parent.getSize();
			thisPos    = new Point ( MathHelper.ceiling_double_int(parentPos.getX() + parentSize.getX() * this.relX / 100D), 
					                 MathHelper.ceiling_double_int(parentPos.getY() + parentSize.getY() * this.relY / 100D));
		}
		
		int x = thisPos.getX();
		int y = thisPos.getY();
		
		if (this.alignX == Align.CENTER)
			x -= this.getSize(parent).getX() / 2;
		
		if (this.alignY == Align.CENTER)
			y -= this.getSize(parent).getY() / 2;
		
		if (this.alignX == Align.RIGHT)
			x -= this.getSize(parent).getX();
		
		if (this.alignY == Align.BOTTOM)
			y -= this.getSize(parent).getY();
		
		return new Point(x,y);
	}
	
	public Point getSize(IWidget parent){
		if (this.isFractional)
			return new Point(this.absSX, this.absSY);
		else {
			Point parentSize = parent.getSize();
			Point thisSize    = new Point ( MathHelper.ceiling_double_int(parentSize.getX() * this.relSX / 100D), 
					                        MathHelper.ceiling_double_int(parentSize.getY() * this.relSY / 100D));
			return thisSize;
		}
	}	
}
