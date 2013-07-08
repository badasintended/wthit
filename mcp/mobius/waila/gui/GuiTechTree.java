package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.widget.DraggableViewport;
import mcp.mobius.waila.gui.widget.StackDisplay;
import net.minecraft.client.gui.GuiScreen;

public class GuiTechTree extends BaseWailaScreen {

	public DraggableViewport widViewport = null;
	public int topOffset = 16;
	
	public GuiTechTree(GuiScreen _parentGui) {
		super(_parentGui);
		
		this.widViewport = new DraggableViewport(this);
		this.widViewport.setWidth(this.res.getScaledWidth() - 32);
		this.widViewport.setHeight(this.res.getScaledHeight() - 32);
		this.widViewport.setBackgroundColor(0x66888888);

		this.addWidget("viewport",    this.widViewport);		
		
	}

	@Override
	public void drawScreen(int i, int j, float f)
	{
	    super.drawScreen(i, j, f);		
		this.widViewport.draw(16,topOffset,0);
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
