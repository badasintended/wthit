package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.widget.ContainerTable;
import mcp.mobius.waila.gui.widget.WikiReader;
import net.minecraft.client.gui.GuiScreen;

public class GuiIngameWiki extends BaseWailaScreen {

	public WikiReader widReader = null;
	
	public GuiIngameWiki(GuiScreen _parentGui) {
		super(_parentGui);
		
		this.widReader = new WikiReader(this);
		this.widReader.setWidth(this.res.getScaledWidth()/2);		
		this.widReader.setHeight(this.res.getScaledHeight());	
		
		this.addWidget("wikireader",    this.widReader);
	}

   @Override
   public void drawScreen(int i, int j, float f)
   {
       super.drawScreen(i, j, f);
       widReader.draw(0, 0, 0);           
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
 