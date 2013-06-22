package mcp.mobius.waila.gui;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.gui.widget.ContainerTable;
import mcp.mobius.waila.gui.widget.Label;
import mcp.mobius.waila.gui.widget.StackDisplay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

public class GUIEnchantScreen extends GuiBaseWailaScreen {

	public ContainerTable table;
	public StackDisplay   stackDisplay;
	public Label          stackName;
	public Label          stackEnchantability;
	
	public GUIEnchantScreen(GuiScreen _parentGui) {
		super(_parentGui);
		this.zLevel = -1.0F;
		//this.zLevel = 500.0F;		
	}
	
   @Override
   /* Here we draw anything special, and super takes care of the buttons for us */
   public void drawScreen(int i, int j, float f)
   {
	   //this.parentGui.drawScreen(i, j, f);
	   //this.parentGui.allowUserInput = false;
	   
       super.drawScreen(i, j, f);
       table.draw(this.getXCentered(table.getWidth()), 32, 0);           
       stackDisplay.draw(this.getXCentered(table.getWidth()), 0, 0);
       stackName.draw(this.getXCentered(table.getWidth())+32, 0, 0);
       stackEnchantability.draw(this.getXCentered(table.getWidth())+32, 8, 0);
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
