package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.CoordType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WidgetAlign;
import net.minecraft.util.MathHelper;

import org.lwjgl.util.Point;



public class WidgetGeometry {
	
	//public enum Align {LEFT, CENTER, RIGHT, TOP, BOTTOM};

	
	double  x = -1;
	double  y = -1;
	double sx = -1;
	double sy = -1;
	
	CoordType posType;
	CoordType sizeType;
	
	boolean fracPosX = false;
	boolean fracPosY = false;
	boolean fracSizeX = false;
	boolean fracSizeY = false;
	
	WidgetAlign alignX;
	WidgetAlign alignY;
	
	public class PointDouble{
		double x; double y;
		public PointDouble(double x, double y){ this.x = x; this.y = y;};
		public double getX(){return this.x;}
		public double getY(){return this.y;}
		public String toString(){return String.format("PointDouble : %.5f %.5f", this.x, this.y);}
	}	
	
	public WidgetGeometry(double x, double y, double sx, double sy, CoordType fracPos, CoordType fracSize){
		this(x, y, sx, sy, fracPos, fracSize, WidgetAlign.LEFT, WidgetAlign.TOP);
	}
	
	public WidgetGeometry(double x, double y, double sx, double sy, CoordType fracPos, CoordType fracSize, WidgetAlign alignX, WidgetAlign alignY){
		this.x = x;
		this.y = y;
		this.sx = sx;
		this.sy = sy;
		this.posType  = fracPos;
		this.sizeType = fracSize;
		
		switch(fracPos){
		case RELX:
			this.fracPosX = true;
			break;
		case RELXY:
			this.fracPosX = true;
			this.fracPosY = true;
			break;
		case RELY:
			this.fracPosY = true;
			break;
		default:
			break;
		
		}

		switch(fracSize){
		case RELX:
			this.fracSizeX = true;
			break;
		case RELXY:
			this.fracSizeX = true;
			this.fracSizeY = true;
			break;
		case RELY:
			this.fracSizeY = true;
			break;
		default:
			break;
		
		}		

		this.alignX = alignX;
		this.alignY = alignY;
	}
	public void setPos(double x, double y){
		this.setPos(x, y, this.fracPosX, this.fracPosY);
	}
	
	public void setPos(double x, double y, boolean fracX, boolean fracY){
		this.x = x;
		this.y = y;
		this.fracPosX = fracX;
		this.fracPosY = fracY;
	}
	
	public Point getPos(IWidget parent){
		
		int x = -1;
		if (this.fracPosX)
			x = MathHelper.ceiling_double_int(parent.getPos().getX() + parent.getSize().getX() * this.x / 100D);
		if (!this.fracPosX && parent != null)
			x = parent.getPos().getX() + (int)this.x;
		if (!this.fracPosX && parent == null)
			x = (int)this.x;
		
		int y = -1;
		if (this.fracPosY)
			y = MathHelper.ceiling_double_int(parent.getPos().getY() + parent.getSize().getY() * this.y / 100D);
		if (!this.fracPosY && parent != null)
			y = parent.getPos().getY() + (int)this.y;
		if (!this.fracPosY && parent == null)
			y = (int)this.y;
		
		
		if (this.alignX == WidgetAlign.CENTER)
			x -= this.getSize(parent).getX() / 2;
		
		if (this.alignY == WidgetAlign.CENTER)
			y -= this.getSize(parent).getY() / 2;
		
		if (this.alignX == WidgetAlign.RIGHT)
			x -= this.getSize(parent).getX();
		
		if (this.alignY == WidgetAlign.BOTTOM)
			y -= this.getSize(parent).getY();
		
		return new Point(x,y);
	}
	
	public Point getSize(IWidget parent){
		int sx = -1;
		if (this.fracSizeX)
			sx = MathHelper.ceiling_double_int(parent.getSize().getX() * this.sx / 100D);
		if (!this.fracSizeX)
			sx = (int)this.sx;
		
		int sy = -1;
		if (this.fracSizeY)
			sy = MathHelper.ceiling_double_int(parent.getSize().getY() * this.sy / 100D);
		if (!this.fracSizeY)
			sy = (int)this.sy;
		
		return new Point(sx,sy);
	}	
}
