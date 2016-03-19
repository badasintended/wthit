package mcp.mobius.waila.gui.testing;

import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.ViewTable;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import net.minecraft.client.gui.GuiScreen;

public class ScreenTest extends ScreenBase {
	
	public ScreenTest(GuiScreen parent) {
		super(parent);

		this.getRoot().addWidget("Test", new ViewTable(null))
			.setGeometry(new WidgetGeometry(50.0, 50.0, 90.0, 90.0, CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
		((ViewTable)this.getRoot().getWidget("Test"))
			.setColumnsTitle("Column1","Column2","Column3")
			.setColumnsWidth(30.0,30.0,30.0)
			.setColumnsAlign(WAlign.CENTER, WAlign.CENTER, WAlign.CENTER)
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3")
			.addRow("test1", "test2", "test3");			
	
		
		/*
		this.getRoot().addWidget("Test", new Row(null)).setGeometry(new WidgetGeometry(50.0, 50.0, 90.0, 20.0, CType.RELXY, CType.REL_X, WAlign.CENTER, WAlign.CENTER));
		((Row)this.getRoot().getWidget("Test")).setColumnsText("aabbbbbbbbbbbbbbbbbbbbbbbbb","bb","cc");
		((Row)this.getRoot().getWidget("Test")).setColumnsWidth(30.0,30.0,30.0);
		((Row)this.getRoot().getWidget("Test")).setColumnsAlign(WAlign.LEFT, WAlign.CENTER, WAlign.CENTER);
		*/
		
		
		//this.getRoot().addWidget("TestLayout", new LayoutBase(null)).setGeometry(new WidgetGeometry(50.0, 50.0, 90.0, 90.0, CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
		//((LayoutBase)this.getRoot().getWidget("TestLayout")).setBackgroundColors(0xff505050, 0xff505050);		
		
		//this.getRoot().addWidget("Layout", new ViewportScrollable(null)).setGeometry(new WidgetGeometry(50.0,50.0,75.0,75.0,CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
		//((ViewportScrollable)this.getRoot().getWidget("Layout")).attachWidget(new PictureSwitch(this.getRoot(), "waila:textures/test.png", "waila:textures/test_solar.png")).setSize(900, 900);
		

	}
}
