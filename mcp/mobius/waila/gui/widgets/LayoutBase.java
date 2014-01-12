package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;

import org.lwjgl.util.Point;

public class LayoutBase extends WidgetBase {

	protected int     bgcolor1 = 0x00000000;
	protected int     bgcolor2 = 0x00000000;
	protected boolean renderBG;
	
	public LayoutBase(IWidget parent){
		super(parent);
	}
	
	@Override
	public void draw(Point pos) {
		if (this.renderBG)
			UIHelper.drawGradientRect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), 0, this.bgcolor1, this.bgcolor2);
	}

	public void setBackgroundColors(int bg1, int bg2){
		this.bgcolor1 = bg1;
		this.bgcolor2 = bg2;
		this.renderBG = true;
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
