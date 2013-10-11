package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.util.MathHelper;

import org.lwjgl.util.Point;



public class WidgetGeometry {
	
	public enum Align {LEFT, CENTER, RIGHT, TOP, BOTTOM};
	
	//double relX = -1;
	//double relY = -1;
	//int    absX = -1;
	//int    absY = -1;

	//double relSX = -1;
	//double relSY = -1;
	//int    absSX = -1;
	//int    absSY = -1;	

	//boolean isAbsolute;
	
	double  x = -1;
	double  y = -1;
	double sx = -1;
	double sy = -1;
	
	boolean fracPos;
	boolean fracSize;
	
	Align alignX;
	Align alignY;
	
	public class PointDouble{
		double x; double y;
		public PointDouble(double x, double y){ this.x = x; this.y = y;};
	}	
	
	public WidgetGeometry(double x, double y, double sx, double sy, boolean fracPos, boolean fracSize){
		this(x, y, sx, sy, fracPos, fracSize, Align.LEFT, Align.TOP);
	}
	
	public WidgetGeometry(double x, double y, double sx, double sy, boolean fracPos, boolean fracSize, Align alignX, Align alignY){
		this.x = x;
		this.y = y;
		this.sx = sx;
		this.sy = sy;
		this.fracPos = fracPos;
		this.fracSize = fracSize;
		this.alignX = alignX;
		this.alignY = alignY;
	}
	
	/*
	public WidgetGeometry(double x, double y, double sx, double sy){
		this(x, y, sx, sy, Align.LEFT, Align.TOP);
	}

	public WidgetGeometry(double x, double y, double sx, double sy, Align alignX, Align alignY){
		this.relX  = x;
		this.relY  = y;
		this.relSX = sx;
		this.relSY = sy;
		this.isAbsolute = false;
		this.alignX = alignX;
		this.alignY = alignY;
	}	
	
	public WidgetGeometry(int x, int y, int sx, int sy){
		this(x, y, sx, sy, Align.LEFT, Align.TOP);
	}	

	public WidgetGeometry(int x, int y, int sx, int sy, Align alignX, Align alignY){
		this.absX  = x;
		this.absY  = y;
		this.absSX = sx;
		this.absSY = sy;
		this.isAbsolute = true;
		this.alignX = alignX;
		this.alignY = alignY;		
	}	
	*/
	
	//public PointDouble getRelativePos() { return new PointDouble(this.relX, this.relY);}
	//public Point       getAbsolutePos() { return new Point(this.absX, this.absY);}
	//public PointDouble getRelativeSize(){ return new PointDouble(this.relSX, this.relSY);}
	//public Point       getAbsoluteSize(){ return new Point(this.absSX, this.absSY);}
	
	public Point getPos(IWidget parent){
		Point thisPos;
		
		if (!this.fracPos && parent != null){
			Point parentPos  = parent.getPos();
			thisPos = new Point(parentPos.getX() + (int)this.x, parentPos.getY() + (int)this.y);
		}
		else if (!this.fracPos && parent == null){
			thisPos = new Point((int)this.x, (int)this.y);
		}
		else {
			Point parentPos  = parent.getPos();
			Point parentSize = parent.getSize();
			thisPos    = new Point ( MathHelper.ceiling_double_int(parentPos.getX() + parentSize.getX() * this.x / 100D), 
					                 MathHelper.ceiling_double_int(parentPos.getY() + parentSize.getY() * this.y / 100D));
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
		if (!this.fracSize)
			return new Point((int)this.sx, (int)this.sy);
		else {
			Point parentSize = parent.getSize();
			Point thisSize    = new Point ( MathHelper.ceiling_double_int(parentSize.getX() * this.sx / 100D), 
					                        MathHelper.ceiling_double_int(parentSize.getY() * this.sy / 100D));
			return thisSize;
		}
	}	
}
