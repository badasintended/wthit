package mcp.mobius.waila.gui.widget;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;

public class ContainerTable {
	public class Row extends ArrayList<String> {}
	
	GuiScreen parentScreen;
	int posX, posY, width, height;
	ArrayList<Integer> columnWidth     = new ArrayList<Integer>();
	ArrayList<String>  columnTitles    = new ArrayList<String>();
	ArrayList<Row> table               = new ArrayList<Row>();	
	
	
	public ContainerTable() {
		// TODO Auto-generated constructor stub
	}

}
