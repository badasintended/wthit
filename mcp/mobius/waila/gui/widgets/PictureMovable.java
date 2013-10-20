package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;

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
		int newX = event.x - this.offsetX;
		int newY = event.y - this.offsetY;
		
		newX = Math.max(newX, this.parent.getLeft());
		newY = Math.max(newY, this.parent.getTop());
		
		if (newX + this.getSize().getX() > this.parent.getRight())
			newX = this.parent.getRight() - this.getSize().getX();

		if (newY + this.getSize().getY() > this.parent.getBottom())
			newY = this.parent.getBottom() - this.getSize().getY();		
		
		this.setGeometry(new WidgetGeometry(newX, newY, this.getSize().getX(), this.getSize().getY(), 
				false, false, this.geom.fracSizeX, this.geom.fracSizeY, this.geom.alignX, this.geom.alignY));
		
		this.emit("onDragX", this.getLeft());
		this.emit("onDragY", this.getTop());
	}
}
