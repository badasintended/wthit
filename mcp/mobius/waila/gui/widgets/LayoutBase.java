package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;

import org.lwjgl.util.Point;

public class LayoutBase extends WidgetBase {

	public LayoutBase(IWidget parent){
		super(parent);
	}
	
	@Override
	public void draw(Point pos) {
		
	}

	/*
	@Override
	public void onMouseEnter(MouseEvent event) {
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseEnter(event);	
	}
	
	@Override
	public void onMouseLeave(MouseEvent event) {
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseLeave(event);	
	}
	*/	
	
}
