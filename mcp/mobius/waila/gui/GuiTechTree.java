package mcp.mobius.waila.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiTechTree extends BaseWailaScreen {

	public GuiTechTree(GuiScreen _parentGui) {
		super(_parentGui);
	}

	@Override
	public void drawScreen(int i, int j, float f)
	{
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
