package mcp.mobius.waila.gui_old;

import mcp.mobius.waila.gui_old.widget.ContainerTable;
import mcp.mobius.waila.gui_old.widget.Label;
import mcp.mobius.waila.gui_old.widget.StackDisplay;
import mcp.mobius.waila.gui_old.widget.WikiReader;
import net.minecraft.client.gui.GuiScreen;

public class GuiIngameWiki extends BaseWailaScreen {

	public WikiReader    widReader = null;
	public StackDisplay   widStack = null;
	public ContainerTable widTable = null;
	public Label       widItemName = null;
	
	public GuiIngameWiki(GuiScreen _parentGui) {
		super(_parentGui);
		
		this.widReader = new WikiReader(this);
		this.widStack  = new StackDisplay(this);
		this.widTable  = new ContainerTable(this);
		this.widItemName = new Label("");
		
		this.widReader.setWidth(this.res.getScaledWidth()-this.res.getScaledWidth()/3 - 16);		
		this.widReader.setHeight(this.res.getScaledHeight() - 24);	
		this.widReader.setBackgroundColor(0x66888888);
		
		this.widStack.setScale(4.0f);

		this.widTable.setAutosize(false);
		this.widTable.setWidth(this.res.getScaledWidth()/3 - 8);
		this.widTable.setHeight(this.res.getScaledHeight() - 16 - this.widStack.getHeight());
		this.widTable.setColumns(8, "\u00a7a\u00a7oEntry", "\u00a7a\u00a7oValue");
		
		this.addWidget("wikireader",    this.widReader);
		this.addWidget("stackdisplay",    this.widStack);
		this.addWidget("datatable",    this.widTable);
	}

   @Override
   public void drawScreen(int i, int j, float f)
   {
       super.drawScreen(i, j, f);
       widTable.draw(4, this.widStack.getHeight() + 16, 1000);
       widItemName.draw(this.res.getScaledWidth()/6-widItemName.getWidth()/2, this.widStack.getHeight(), 0);
       widReader.draw(this.res.getScaledWidth()/3 + 8, 8, 0);
       widStack.draw(this.res.getScaledWidth()/6-widStack.getWidth()/2, 0, 0);
   }

   @Override
   public void initGui()
   {
   }
   
   @Override
   public void drawDefaultBackground()
   {
	   this.drawGradientRect(0, 0, this.width, this.height, 0xdd000000, 0xdd000000);
   }	
	
}
 