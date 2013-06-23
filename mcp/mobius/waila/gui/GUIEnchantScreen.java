package mcp.mobius.waila.gui;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.gui.widget.ContainerTable;
import mcp.mobius.waila.gui.widget.Label;
import mcp.mobius.waila.gui.widget.StackDisplay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

public class GUIEnchantScreen extends GuiBaseWailaScreen {

	public ContainerTable widTable;
	public StackDisplay   widStackDisplay;
	public Label          widStackName;
	public Label          widStackEnchantability;
	
	public GUIEnchantScreen(GuiScreen _parentGui) {
		super(_parentGui);
		this.zLevel = -1.0F;
		
		this.widTable               = new ContainerTable(this);				
		this.widStackDisplay        = new StackDisplay(this);
		this.widStackName           = new Label(this);
		this.widStackEnchantability = new Label(this);
		
		this.widTable.setColumns(8, "\u00a7a\u00a7oName", "\u00a7a\u00a7oMin lvl", "\u00a7a\u00a7oMax lvl", "\u00a7a\u00a7oMod");
		
		this.addWidget("datatable",    this.widTable);
		this.addWidget("stackdisplay", this.widStackDisplay);
		this.addWidget("stackname",    this.widStackName);
		this.addWidget("stackenchant", this.widStackEnchantability);
	}
	
   @Override
   /* Here we draw anything special, and super takes care of the buttons for us */
   public void drawScreen(int i, int j, float f)
   {
	   //this.parentGui.drawScreen(i, j, f);
	   //this.parentGui.allowUserInput = false;
	   
       super.drawScreen(i, j, f);
       widTable.draw(this.getXCentered(widTable.getWidth()), 32, 0);           
       widStackDisplay.draw(this.getXCentered(widTable.getWidth()), 0, 0);
       widStackName.draw(this.getXCentered(widTable.getWidth())+32, 0, 0);
       widStackEnchantability.draw(this.getXCentered(widTable.getWidth())+32, 8, 0);
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