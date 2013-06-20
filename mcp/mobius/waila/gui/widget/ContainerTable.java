package mcp.mobius.waila.gui.widget;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;

public class ContainerTable {
	GuiScreen parentScreen;
	int posX, posY, width, height, startRow, endRow;
	ArrayList<Integer>  columnWidth     = new ArrayList<Integer>();
	ArrayList<String>   columnTitles    = new ArrayList<String>();
	ArrayList<String[]> table               = new ArrayList<String[]>();	
	
	
	public ContainerTable(int _posX, int _posY, int _width, int _height, ArrayList<String> _titles) {
		this.posX     = _posX;
		this.posY     = _posY;
		this.width    = _width;
		this.height   = _height;
		this.columnTitles = _titles;
		this.startRow = 0;
		
	}

}
