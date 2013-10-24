package mcp.mobius.waila.gui.screens.info;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.ItemStackDisplay;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.ViewTable;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public class ScreenEnchants extends ScreenBase {
	
	public ScreenEnchants(GuiScreen parent) {
		super(parent);

		this.getRoot().addWidget("Layout_Title", new LayoutBase(null))
					  .setGeometry(new WidgetGeometry(50.0, 10.0, 50.0, 32.0, CType.REL_X, CType.REL_X, WAlign.CENTER, WAlign.TOP));
		
		this.getRoot().getWidget("Layout_Title").addWidget("ItemStack", new ItemStackDisplay(null))
		              .setGeometry(new WidgetGeometry(0.0, 0.0, 32.0, 32.0, CType.ABSXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));

		this.getRoot().getWidget("Layout_Title").addWidget("LabelName", new LabelFixedFont(null, "None"))
        			  .setGeometry(new WidgetGeometry(40.0, 4.0, 16.0, 16.0, CType.ABSXY, CType.ABSXY, WAlign.LEFT, WAlign.TOP));		

		this.getRoot().getWidget("Layout_Title").addWidget("LabelEnchantability", new LabelFixedFont(null, "None"))
		  .setGeometry(new WidgetGeometry(40.0, 22.0, 16.0, 16.0, CType.ABSXY, CType.REL_X, WAlign.LEFT, WAlign.BOTTOM));		
		
		this.getRoot().addWidget("Table", new ViewTable(null))
			.setGeometry(new WidgetGeometry(50.0, 50.0, 90.0, 90.0, CType.REL_X, CType.RELXY, WAlign.CENTER, WAlign.TOP));

		((ViewTable)this.getRoot().getWidget("Table"))
			.setColumnsTitle("\u00a7a\u00a7oName","\u00a7a\u00a7oMin lvl","\u00a7a\u00a7oMax lvl","\u00a7a\u00a7oWeight", "\u00a7a\u00a7oMod")
			.setColumnsWidth(35.0, 10.0, 10.0, 10.0, 35.0)
			.setColumnsAlign(WAlign.LEFT, WAlign.CENTER, WAlign.CENTER, WAlign.CENTER, WAlign.LEFT);
	}
	
	public ViewTable getTable(){
		return (ViewTable)this.getRoot().getWidget("Table");
	}
	
	public ScreenEnchants addRow(String...strings ){
		this.getTable().addRow(strings);
		return this;
	}
	
	public ScreenEnchants setStack(ItemStack stack){
		((ItemStackDisplay)this.getRoot().getWidget("Layout_Title").getWidget("ItemStack")).setStack(stack);
		this.getRoot().getWidget("Layout_Title").adjustSize();		
		return this;
	}
	
	public ScreenEnchants setName(String name){
		((LabelFixedFont)this.getRoot().getWidget("Layout_Title").getWidget("LabelName")).setText(name);
		this.getRoot().getWidget("Layout_Title").adjustSize();
		return this;
	}
	
	public ScreenEnchants setEnchantability(String value){
		((LabelFixedFont)this.getRoot().getWidget("Layout_Title").getWidget("LabelEnchantability")).setText(String.format("Enchantability : %s", value));
		this.getRoot().getWidget("Layout_Title").adjustSize();
		return this;
	}	
	
	/*
	public ScreenEnchants setStack(ItemStack stack){
		((ItemStackDisplay)this.getRoot().getWidget("ItemStack")).setStack(stack);
		return this;
	}
	*/	
}	
