package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.WidgetBase;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.WidgetGeometry.Align;

import org.lwjgl.util.Point;

//TODO : THIS CLASS IS WIP AND CAN'T WORK RIGHT NOW

public class ButtonContainer extends WidgetBase {

	private int nButtons = 0;
	private int columns;
	private int buttonSize;
	private double spacing;
	
	public ButtonContainer(IWidget parent, int columns, int buttonSize, double spacing){
		super (parent);
		this.columns = columns;
		this.spacing = spacing;
		this.buttonSize = buttonSize;
	}
	
	public void addButton(ButtonBase button){
		String buttonName = String.format("Button_%d", nButtons);
		String layoutName = String.format("Layout_%d", nButtons);
		
		this.addWidget(layoutName, new LayoutBase(this));
		
		int column = this.nButtons % this.columns;
		int row    = this.nButtons / this.columns;
		double sizeColumn = 100.0 / this.columns;
		
		this.getWidget(layoutName).setGeometry(new WidgetGeometry(sizeColumn * column, spacing * row, sizeColumn, spacing, true, true));
		this.getWidget(layoutName).addWidget(buttonName, button);
		this.getWidget(layoutName).getWidget(buttonName).setGeometry(new WidgetGeometry(50.0, 50.0, buttonSize, 20.0, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));;
		
		this.nButtons += 1;
	}
	
	@Override
	public void draw(Point pos) {}

}
