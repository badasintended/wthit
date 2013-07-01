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
		
		this.widReader.setText("The quick brown fox jumps \n\n\n\n\nover the \nlazy \ndog \nThe \nq\nu\ni\nc\nk\n \nb\nrown \n\n\nf\nox\n \nj\nu\nm\np\ns\n \n\nn\no\nver the\n lazy \ndog \nThe \nquick \nbrown \nfox \njumps\n over the lazy dog The quick brown fox jumps \nover the lazy dog ");
		for (String s : this.widReader.getTailoredText()){
			System.out.println(s);
		}
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
 