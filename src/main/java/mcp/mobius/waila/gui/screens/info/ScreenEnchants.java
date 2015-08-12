package mcp.mobius.waila.gui.screens.info;

import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

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
			.setGeometry(new WidgetGeometry(50.0, 50.0, 90.0, 80.0, CType.REL_X, CType.RELXY, WAlign.CENTER, WAlign.TOP));

		String columnName  = "\u00a7a\u00a7o" + LangUtil.translateG("enchant.title.name");
		String columnMinLvl= "\u00a7a\u00a7o" + LangUtil.translateG("enchant.title.minlvl");;
		String columnMaxLvl= "\u00a7a\u00a7o" + LangUtil.translateG("enchant.title.maxlvl");;
		String columnWeight= "\u00a7a\u00a7o" + LangUtil.translateG("enchant.title.weight");;
		
		((ViewTable)this.getRoot().getWidget("Table"))
			.setColumnsTitle(columnName, columnMinLvl, columnMaxLvl, columnWeight, "\u00a7a\u00a7oMod")
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
		((LabelFixedFont)this.getRoot().getWidget("Layout_Title").getWidget("LabelEnchantability")).setText(String.format("%s : %s", LangUtil.translateG("enchant.label.enchantability"), value));
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
