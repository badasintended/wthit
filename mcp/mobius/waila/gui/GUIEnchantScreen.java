package mcp.mobius.waila.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

public class GUIEnchantScreen extends GuiBaseWailaScreen {

	public GUIEnchantScreen(GuiScreen _parentGui) {
		super(_parentGui);
		this.zLevel = 500.0F;		
	}
	
   @Override
   /* Here we draw anything special, and super takes care of the buttons for us */
   public void drawScreen(int i, int j, float f)
   {
	   this.parentGui.drawScreen(i, j, f);
	   this.parentGui.allowUserInput = false;
	   
	   this.drawGradientRect(0, 0, this.width, this.height, 0x3fefeff0, 0x3fefeff0);        
       super.drawScreen(i, j, f);
   }
}
