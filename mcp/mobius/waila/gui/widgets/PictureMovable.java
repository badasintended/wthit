package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.events.Signal;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.WidgetGeometry.PointDouble;

import org.lwjgl.util.Point;

public class PictureMovable extends PictureDisplay {

	private int offsetX, offsetY;
	
	public PictureMovable(IWidget parent, String uri) {
		super(parent, uri);
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		this.offsetX = event.x - this.getPos().getX();
		this.offsetY = event.y - this.getPos().getY();
	}
	
	@Override	
	public void onMouseDrag(MouseEvent event){
		double newX = event.x - this.offsetX;
		double newY = event.y - this.offsetY;
		
		newX = Math.max(newX, this.parent.getLeft());
		newY = Math.max(newY, this.parent.getTop());
		
		if (newX + this.getSize().getX() > this.parent.getRight())
			newX = this.parent.getRight() - this.getSize().getX();

		if (newY + this.getSize().getY() > this.parent.getBottom())
			newY = this.parent.getBottom() - this.getSize().getY();		
		
		this.geom.setPos(newX, newY);
		
		
		
		//this.setGeometry(new WidgetGeometry(newX, newY, this.getSize().getX(), this.getSize().getY(), 
		//		false, false, this.geom.fracSizeX, this.geom.fracSizeY, this.geom.alignX, this.geom.alignY));
		
		this.emit(Signal.DRAGGED, this.getPos());
	}
}
