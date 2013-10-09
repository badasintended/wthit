package mcp.mobius.waila.gui.interfaces;

import mcp.mobius.waila.gui.widgets.WidgetGeometry;

import org.lwjgl.util.Point;

public interface IWidget {
	// Should all the coordinates, sizes, etc be % or absolute values ?
	// Should we allow 2 methods for an absolute referencing (screen pos) and relative one (parent pos) ?
	
	IWidget getParent();
	void    setParent(IWidget parent);
	
	void setGeometry(WidgetGeometry geom);
	WidgetGeometry getGeometry();
	
	Point getPos();
	Point getSize();
	
	void  draw();
	void  draw(Point pos);
	
	IWidget addWidget(String name, IWidget widget);
	IWidget getWidget(String name);
	IWidget delWidget(String name);	
}
