package mcp.mobius.waila.gui.screens.info;

import net.minecraft.client.gui.GuiScreen;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.ViewTable;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public class ScreenEnchants extends ScreenBase {
	
	public ScreenEnchants(GuiScreen parent) {
		super(parent);

		this.getRoot().addWidget("Table", new ViewTable(null))
			.setGeometry(new WidgetGeometry(50.0, 50.0, 90.0, 90.0, CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));

		((ViewTable)this.getRoot().getWidget("Table"))
			.setColumnsTitle("\u00a7a\u00a7oName","\u00a7a\u00a7oMin lvl","\u00a7a\u00a7oMax lvl", "\u00a7a\u00a7oMod")
			.setColumnsWidth(25.0, 25.0, 25.0, 25.0)
			.setColumnsAlign(WAlign.LEFT, WAlign.CENTER, WAlign.CENTER, WAlign.LEFT);
	}
	
	public ViewTable getTable(){
		return (ViewTable)this.getRoot().getWidget("Table");
	}
	
	public ScreenEnchants addRow(String...strings ){
		this.getTable().addRow(strings);
		return this;
	}
}
