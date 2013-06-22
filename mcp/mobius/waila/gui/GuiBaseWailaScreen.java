package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.widget.Button2States;
import mcp.mobius.waila.gui.widget.ButtonChangeScreen;
import mcp.mobius.waila.gui.widget.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiBaseWailaScreen extends GuiScreen {
 
    protected GuiScreen parentGui;	// GUI we will return to in the case we are call from a GUI
	protected ScaledResolution res;
    
	public GuiBaseWailaScreen(GuiScreen _parentGui) {
		this.parentGui = _parentGui;
        res = new ScaledResolution(Minecraft.getMinecraft().gameSettings, 
        		                   Minecraft.getMinecraft().displayWidth, 
        		                   Minecraft.getMinecraft().displayHeight);		
	}

   @Override
   /* Here we draw anything special, and super takes care of the buttons for us */
   public void drawScreen(int i, int j, float f)
   {
       drawDefaultBackground();        
       super.drawScreen(i, j, f);
   }   

   @Override
   /* Here we handle key presses. Super will just close the gui on ESC, and we like it like that */
   public void keyTyped(char keyChar, int keyID)
   {
       if (keyID == 1)
       {
    	   if (this.parentGui == null){
    		   this.mc.displayGuiScreen((GuiScreen)null);
    		   this.mc.setIngameFocus();
    	   } else {
    		   this.mc.displayGuiScreen(this.parentGui);
    	   }
       }	   
   }   
   
   @Override
   protected void actionPerformed(GuiButton guibutton)
   {
	   if (guibutton instanceof ButtonChangeScreen)
		   ((ButtonChangeScreen)guibutton).pressButton();
	   
	   else if (guibutton instanceof Button2States)
		   ((Button2States)guibutton).pressButton();	   
   }
   
   protected int getXCentered(int width){
	   return (this.res.getScaledWidth()  - width)/ 2;
   }
   
}